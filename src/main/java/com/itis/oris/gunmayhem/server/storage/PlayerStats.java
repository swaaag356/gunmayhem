package com.itis.oris.gunmayhem.server.storage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerStats {
    private int wins;
    private int losses;
}

