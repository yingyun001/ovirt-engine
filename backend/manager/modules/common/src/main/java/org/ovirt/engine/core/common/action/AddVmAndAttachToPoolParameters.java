package org.ovirt.engine.core.common.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.ovirt.engine.core.common.businessentities.VmStatic;
import org.ovirt.engine.core.common.businessentities.storage.DiskImage;
import org.ovirt.engine.core.compat.Guid;

public class AddVmAndAttachToPoolParameters extends AddVmParameters {
    private static final long serialVersionUID = -2676528333942591702L;

    private Guid poolId;
    private String currentVmName;

    public AddVmAndAttachToPoolParameters() {
    }

    public AddVmAndAttachToPoolParameters(VmStatic currVm,
            Guid poolId,
            String currentVmName,
            HashMap<Guid, DiskImage> diskInfoDestinationMap) {
        super(currVm);
        setDiskInfoList(new ArrayList<DiskImage>(Arrays.asList(new DiskImage())));
        this.poolId = poolId;
        this.currentVmName = currentVmName;
        setDiskInfoDestinationMap(diskInfoDestinationMap);
    }

    public Guid getPoolId() {
        return poolId;
    }

    public String getCurrentVmName() {
        return currentVmName;
    }
}
