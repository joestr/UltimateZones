package net.dertod2.UltimateZones.Classes;

import org.bukkit.Material;
import org.bukkit.material.MaterialData;

@SuppressWarnings("deprecation")
public class RightResult {
    public static RightResult Success = new RightResult(RightEnum.Success);
    public static RightResult Missing_Block = new RightResult(RightEnum.Missing_Block);
    public static RightResult Missing_Right = new RightResult(RightEnum.Missing_Right);

    public final RightEnum rightEnum;
    public Flag flag;
    public MaterialData materialData;

    public RightResult(final RightEnum rightEnum) {
        this.rightEnum = rightEnum;
    }

    public RightResult setBoth(Flag flag, Material material) {
        this.flag = flag;
        this.materialData = new MaterialData(material); // TODO Maybe allow better control of subIds later

        return this;
    }

    public enum RightEnum {
        Success, Missing_Right, Missing_Block;
    }
}