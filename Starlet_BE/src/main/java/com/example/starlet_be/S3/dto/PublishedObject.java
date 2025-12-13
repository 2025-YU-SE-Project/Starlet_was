package com.example.starlet_be.S3.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "of")
public class PublishedObject {
    private final String key;
    private final String url;
}
