package com.example.model.kibana;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class KibanaResponse {
    @JsonProperty("_index")
    private String index;

    @JsonProperty("_id")
    private String id;

    @JsonProperty("_version")
    private int version;

    @JsonProperty("result")
    private String result;

    @JsonProperty("_shards")
    private KibanaShardResponse shards;

    @JsonProperty("_seq_no")
    private int seqNo;

    @JsonProperty("_primary_term")
    private int primaryTerm;

    private String error;
    private String status;
}
