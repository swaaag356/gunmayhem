package com.itis.oris.gunmayhem.common.protocol.payloads;

import com.itis.oris.gunmayhem.common.model.enums.MagicType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CastPayload {

    private int playerId;

    private MagicType magicType;
}

