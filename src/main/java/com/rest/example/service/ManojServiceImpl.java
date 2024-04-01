package com.rest.example.service;

import com.google.gson.Gson;
import com.rest.example.DTO.APIResonseDTO;
import com.rest.example.DTO.BancsRequestDto;
import com.rest.example.DTO.EmployeeDetailsDTO;
import com.rest.example.config.EnvironmentVariables;
import com.rest.example.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.signer.Aws4Signer;
import software.amazon.awssdk.auth.signer.params.Aws4SignerParams;
import software.amazon.awssdk.http.*;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.utils.IoUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

import static com.rest.example.exception.ErrorDetails.SERVICE_UNAVAILABLE;

@Service
@RequiredArgsConstructor
public class ManojServiceImpl implements ManojService {

    private final EnvironmentVariables environmentVariables;

    private final Gson gson;
    @Override
    public APIResonseDTO uploadData(EmployeeDetailsDTO employeeDetailsDTO, List<MultipartFile> multipartFileList) {

        BancsRequestDto requestDTO = buildDepartmentRequestDTO(employeeDetailsDTO);
        callExternalAPI(requestDTO);

        return APIResonseDTO.builder().statusCode(200).responseBody("Success").build();
    }

    private BancsRequestDto buildDepartmentRequestDTO(EmployeeDetailsDTO employeeDetails){
        return BancsRequestDto.builder().empId(employeeDetails.getEmpId())
                .name(employeeDetails.getName())
                .mobNumber(employeeDetails.getMobNumber())
                .build();

    }

    private APIResonseDTO callExternalAPI(final BancsRequestDto bancsRequest){

        SdkHttpFullRequest sdkHttpFullRequest = environmentVariables.isAwsAuth()?
                signRequest(buildBancsUnsignedRequest(bancsRequest)) : buildBancsSignedRequest(bancsRequest);
        return getHttpResponse(sdkHttpFullRequest);
    }

    private APIResonseDTO getHttpResponse(SdkHttpFullRequest sdkHttpFullRequest) {
        SdkHttpClient httpClient = ApacheHttpClient.builder().connectionTimeout(Duration.ofSeconds(15)).build();
        HttpExecuteRequest executeRequest = HttpExecuteRequest.builder().request(sdkHttpFullRequest)
                .contentStreamProvider(sdkHttpFullRequest.contentStreamProvider().orElse(null)).build();
        HttpExecuteResponse httpResponse;
        String responseBody = null;
        try{
            httpResponse = getHttpExecuteResponse(executeRequest, httpClient);
            Optional<AbortableInputStream> httpResponseBody = httpResponse.responseBody();
            if(httpResponseBody.isPresent()){
                responseBody = IoUtils.toUtf8String(httpResponseBody.get());
            }
        }catch (IOException ioException){
            throw new GlobalException(SERVICE_UNAVAILABLE);
        }
        return APIResonseDTO.builder()
                .statusCode(httpResponse.httpResponse().statusCode())
                .responseBody(responseBody).build();
    }

    private HttpExecuteResponse getHttpExecuteResponse(HttpExecuteRequest executeRequest, SdkHttpClient httpClient) throws IOException {
        return httpClient.prepareRequest(executeRequest).call();
    }

    private SdkHttpFullRequest signRequest(final SdkHttpFullRequest sdkHttpFullRequest){
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

    private SdkHttpFullRequest buildBancsSignedRequest(BancsRequestDto bancsRequest){
        try {
            return SdkHttpFullRequest.builder()
                    .method(SdkHttpMethod.POST)
                    .uri(new URI(""))
                    .putHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .contentStreamProvider(createContentStream(gson.toJson(bancsRequest).getBytes()))
                    .build();
        }catch (URISyntaxException e){
            throw new GlobalException(SERVICE_UNAVAILABLE);
        }
    }

    private SdkHttpFullRequest buildBancsUnsignedRequest(BancsRequestDto bancsRequest){
        try {
            return SdkHttpFullRequest.builder()
                    .method(SdkHttpMethod.POST)
                    .uri(new URI(""))
                    .putHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .contentStreamProvider(createContentStream(gson.toJson(bancsRequest).getBytes()))
                .build();
        }catch (URISyntaxException e){
            throw new GlobalException(SERVICE_UNAVAILABLE);
        }
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
