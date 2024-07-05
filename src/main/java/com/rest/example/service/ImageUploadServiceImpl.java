package com.rest.example.service;

import com.rest.example.DTO.APIResonseDTO;
import com.rest.example.config.EnvironmentVariables;
import com.rest.example.exception.GlobalException;
import com.rest.example.util.CustomMultipartRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.signer.Aws4Signer;
import software.amazon.awssdk.auth.signer.params.Aws4SignerParams;
import software.amazon.awssdk.http.*;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.utils.IoUtils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.Objects;
import java.util.Optional;

import static com.rest.example.exception.ErrorDetails.SERVICE_UNAVAILABLE;


@Service
@AllArgsConstructor
@Slf4j
public class ImageUploadServiceImpl implements ImageUploadService {

    private final EnvironmentVariables environmentVariables;


    @Override
    public APIResonseDTO uploadImage(final MultipartFile multipartFile) {

        CustomMultipartRequest customMultipartRequest = buildMultipartRequest(multipartFile);
        SdkHttpFullRequest sdkHttpFullRequest = environmentVariables.isAwsAuth()?
                signRequest(buildBancsSignedRequest(customMultipartRequest)) : buildBancsUnsignedRequest(customMultipartRequest);
        return getHttpResponse(sdkHttpFullRequest);
    }

    private CustomMultipartRequest buildMultipartRequest(MultipartFile multipartFile) {
        String originalFileName = multipartFile.getOriginalFilename();
        String fileExtension = StringUtils.getFilenameExtension(originalFileName);
        MultiValueMap<String, Object> requestPart= new LinkedMultiValueMap<>();
        requestPart.add("File Name", Objects.requireNonNull(originalFileName));
        requestPart.add("File Type", fileExtension);
        try {
            requestPart.add("Image", getMultipartFile(multipartFile.getBytes(), originalFileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return buildCustomMultipartRequest(requestPart);
    }

    private CustomMultipartRequest buildCustomMultipartRequest(MultiValueMap<String, Object> requestPart) {
        try{
            CustomMultipartRequest customMultipartRequest = new CustomMultipartRequest();
            new FormHttpMessageConverter().write(requestPart, MediaType.MULTIPART_FORM_DATA, customMultipartRequest);
            return customMultipartRequest;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Resource getMultipartFile(final byte[] fileContent, String originalFileName) {

        return new ByteArrayResource(fileContent) {

            @Override
            public String getFilename() {return originalFileName;}
        };
    }

    private SdkHttpFullRequest signRequest(final SdkHttpFullRequest sdkHttpFullRequest){
        log.info("Building Signed Request");
        Aws4Signer signer = Aws4Signer.create();
        Aws4SignerParams signerParams;
        try(DefaultCredentialsProvider defaultCredentialsProvider = DefaultCredentialsProvider.create()){
            signerParams = Aws4SignerParams.builder()
                    .signingRegion(Region.AP_NORTHEAST_1)
                    .awsCredentials(defaultCredentialsProvider.resolveCredentials())
                    .signingName("execute-api")
                    .build();
        }
        return signer.sign(sdkHttpFullRequest, signerParams);
    }

    private SdkHttpFullRequest buildBancsSignedRequest(CustomMultipartRequest customMultipartRequest){
        try {
            return SdkHttpFullRequest.builder()
                    .method(SdkHttpMethod.POST)
                    .uri(new URI("http://localhost:8085/mock/image/upload"))
                    .putHeader(HttpHeaders.CONTENT_TYPE, String.valueOf(customMultipartRequest.getHeaders().getContentType()))
                    .contentStreamProvider(createContentStream(
                            ((ByteArrayOutputStream) customMultipartRequest.getBody()).toByteArray()))
                    .build();
        }catch (URISyntaxException | IOException ex){
            throw new GlobalException(SERVICE_UNAVAILABLE);
        }
    }

    private SdkHttpFullRequest buildBancsUnsignedRequest(CustomMultipartRequest customMultipartRequest){
        log.info("Building Unsigned Request");
        try {
            return SdkHttpFullRequest.builder()
                    .method(SdkHttpMethod.POST)
                    .uri(new URI("http://localhost:8085/mock/response"))
                    .putHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .contentStreamProvider(createContentStream(
                            ((ByteArrayOutputStream) customMultipartRequest.getBody()).toByteArray()))
                    .build();
        }catch (URISyntaxException  | IOException ex){
            throw new GlobalException(SERVICE_UNAVAILABLE);
        }
    }

    private APIResonseDTO getHttpResponse(SdkHttpFullRequest sdkHttpFullRequest) {
        SdkHttpClient httpClient = ApacheHttpClient.builder().connectionTimeout(Duration.ofSeconds(15)).build();
        HttpExecuteRequest executeRequest = HttpExecuteRequest.builder().request(sdkHttpFullRequest)
                .contentStreamProvider(sdkHttpFullRequest.contentStreamProvider().orElse(null)).build();
        HttpExecuteResponse httpResponse;
        String responseBody = null;
        try{
            log.info("Calling External API ");
            httpResponse = getHttpExecuteResponse(executeRequest, httpClient);
            log.info("Calling External API is Completed");
            Optional<AbortableInputStream> httpResponseBody = httpResponse.responseBody();
            if(httpResponseBody.isPresent()){
                responseBody = IoUtils.toUtf8String(httpResponseBody.get());
            }
        }catch (IOException ioException){
            throw new GlobalException(SERVICE_UNAVAILABLE);
        }
        return APIResonseDTO.builder()
                .statusCode(httpResponse.httpResponse().statusCode())
                .responseBody(responseBody)
                .build();
    }

    private HttpExecuteResponse getHttpExecuteResponse(HttpExecuteRequest executeRequest, SdkHttpClient httpClient) throws IOException {
        return httpClient.prepareRequest(executeRequest).call();
    }

    private ContentStreamProvider createContentStream(final byte[] payload){
        return () -> {
            try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(payload)){
                return byteArrayInputStream;
            }catch (IOException ex){
                throw new GlobalException(SERVICE_UNAVAILABLE);
            }
        };
    }
}
