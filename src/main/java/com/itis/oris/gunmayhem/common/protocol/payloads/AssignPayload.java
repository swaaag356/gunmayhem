package com.itis.oris.gunmayhem.common.protocol.payloads;

import com.itis.oris.gunmayhem.common.model.enums.MageType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignPayload {

    private int playerId;

    private MageType role;
}
