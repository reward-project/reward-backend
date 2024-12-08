@Configuration
public class TossConfig {
    @Value("${toss.client.api-key}")
    private String apiKey;
    
    @Value("${toss.client.secret-key}")
    private String secretKey;
    
    @Bean
    public RestTemplate tossRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        return restTemplate;
    }
} 