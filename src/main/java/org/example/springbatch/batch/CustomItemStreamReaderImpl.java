package org.example.springbatch.batch;

import org.springframework.batch.item.*;
import org.springframework.web.client.RestTemplate;

/**
 * ItemStream 을 이해하기 위한 예시 코드 입니다.
 * 데이터베이스 - 엑셀 이 아닌 url 요청 혹은 별도에 파일 작업이 필요할 때는 ItemStreamReader를 상속 받아서 구현해야 합니다.
 * 트래킹을 확인해야합니다. 일반적인 블로그에서 read() 수행하는데 좋지 않습니다.
 */
public class CustomItemStreamReaderImpl implements ItemStreamReader<String> {

    private final RestTemplate restTemplate;
    private int currentId;
    private final String CURRENT_ID_KEY = "current.call.id";
    private final String API_URL = "https://www.naver.com/page?id=";

    public CustomItemStreamReaderImpl(RestTemplate restTemplate) {

        this.currentId = 0;
        this.restTemplate = restTemplate;
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {

        if (executionContext.containsKey(CURRENT_ID_KEY)) {
            currentId = executionContext.getInt(CURRENT_ID_KEY);
        }
    }

    @Override
    public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {

        currentId++;

        String url = API_URL + currentId;
        String response = restTemplate.getForObject(url, String.class);

        if (response == null) {
            return null;
        }
        return response;
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        executionContext.putInt(CURRENT_ID_KEY, currentId);
    }

    @Override
    public void close() throws ItemStreamException {

    }
}
