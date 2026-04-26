package com.saquib.apts.DTO;

import com.saquib.apts.Enums.SpotType;
import lombok.Data;

@Data
public class BulkSpotRequest {
    private SpotType spotType;
    private int count;
    private String prefix;
}
