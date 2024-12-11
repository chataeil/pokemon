package org.koreait.file.entities;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;


@Data
@RedisHash(value = "test_hash", timeToLive = 300) // redis key & value 저장 메모리 기반 DB 새션을 저장하는 기술
public class RedisItem implements Serializable {
    @Id
    private String key;
    private int price;
    private String productNm;

}

