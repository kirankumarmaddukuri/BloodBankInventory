package com.bloodbank.util;

import com.bloodbank.entity.enums.BloodGroup;

import java.util.Arrays;
import java.util.List;

public class CompatibilityUtil {

    public static List<BloodGroup> getCompatibleDonorGroups(BloodGroup recipientGroup) {
        switch (recipientGroup) {
            case O_POS:
                return Arrays.asList(BloodGroup.O_POS, BloodGroup.O_NEG);
            case A_POS:
                return Arrays.asList(BloodGroup.A_POS, BloodGroup.A_NEG, BloodGroup.O_POS, BloodGroup.O_NEG);
            case B_POS:
                return Arrays.asList(BloodGroup.B_POS, BloodGroup.B_NEG, BloodGroup.O_POS, BloodGroup.O_NEG);
            case AB_POS:
                return Arrays.asList(BloodGroup.values()); // Can receive from all
            case O_NEG:
                return Arrays.asList(BloodGroup.O_NEG);
            case A_NEG:
                return Arrays.asList(BloodGroup.A_NEG, BloodGroup.O_NEG);
            case B_NEG:
                return Arrays.asList(BloodGroup.B_NEG, BloodGroup.O_NEG);
            case AB_NEG:
                return Arrays.asList(BloodGroup.AB_NEG, BloodGroup.A_NEG, BloodGroup.B_NEG, BloodGroup.O_NEG);
            default:
                throw new IllegalArgumentException("Unknown blood group");
        }
    }

    public static boolean canReceive(BloodGroup recipient, BloodGroup donor) {
        return getCompatibleDonorGroups(recipient).contains(donor);
    }
}
