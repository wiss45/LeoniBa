package com.sip.interfaces;

import com.sip.enums.StatutEquipement;

public interface StatutCountPerProjet {
    Long getProjetId();
    String getProjetName();
    StatutEquipement getStatut();
    Long getCount();
}