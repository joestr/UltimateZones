package net.dertod2.UltimateZones.Classes.Control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;

import net.dertod2.DatabaseHandler.Binary.DatabaseHandler;
import net.dertod2.DatabaseHandler.Table.TableEntry;
import net.dertod2.UltimateZones.Classes.Preset;
import net.dertod2.UltimateZones.Classes.RightReference;

public class PresetControl {
    private Map<String, Preset> presetList;

    public PresetControl() {
        try {
            DatabaseHandler.get().getHandler().updateLayout(new Preset());
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    public void load() {
        List<TableEntry> dataList = new ArrayList<TableEntry>();

        if (this.presetList == null)
            this.presetList = new HashMap<String, Preset>();

        this.presetList.clear();

        try {
            DatabaseHandler.get().getHandler().load(new Preset(), dataList);
        } catch (Exception exc) {
            exc.printStackTrace();
        } finally {
            for (TableEntry tableEntry : dataList) {
                Preset preset = (Preset) tableEntry;
                this.presetList.put(preset.getName().toLowerCase(), preset);
            }
        }
    }

    public Preset getPreset(String presetName) {
        return this.presetList.get(presetName.toLowerCase());
    }

    public List<Preset> getPresets() {
        return ImmutableList.<Preset>copyOf(this.presetList.values());
    }

    public Preset createPreset(String presetName, List<Integer> flagList, List<String> breakList,
            List<String> placeList) {
        if (this.presetList.containsKey(presetName.toLowerCase()))
            return null;

        Preset preset = new Preset(presetName, flagList, breakList, placeList);
        try {
            DatabaseHandler.get().getHandler().insert(preset);
        } catch (Exception exc) {
            exc.printStackTrace();
            return null;
        }

        this.presetList.put(presetName.toLowerCase(), preset);
        return preset;
    }

    public void delPreset(Preset preset) {
        try {
            DatabaseHandler.get().getHandler().remove(preset);
        } catch (Exception exc) {
            exc.printStackTrace();
        } finally {
            this.presetList.remove(preset.getName().toLowerCase());
            RightReference.refreshPresetName(preset.getName(), "");
        }
    }
}