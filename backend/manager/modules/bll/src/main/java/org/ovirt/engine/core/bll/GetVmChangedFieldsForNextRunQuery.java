package org.ovirt.engine.core.bll;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.ovirt.engine.core.common.businessentities.VM;
import org.ovirt.engine.core.common.businessentities.VMStatus;
import org.ovirt.engine.core.common.businessentities.VmStatic;
import org.ovirt.engine.core.common.queries.GetVmChangedFieldsForNextRunParameters;
import org.ovirt.engine.core.common.utils.SimpleDependecyInjector;
import org.ovirt.engine.core.common.utils.VmDeviceUpdate;
import org.ovirt.engine.core.common.utils.customprop.VmPropertiesUtils;

public class GetVmChangedFieldsForNextRunQuery<P extends GetVmChangedFieldsForNextRunParameters>
        extends QueriesCommandBase<P>{

    public GetVmChangedFieldsForNextRunQuery(P parameters) {
        super(parameters);
    }

    @Override
    protected void executeQueryCommand() {
        VM srcVm = getParameters().getOriginal();
        VM dstVm = getParameters().getUpdated();
        VmStatic srcStatic = srcVm.getStaticData();
        VmStatic dstStatic = dstVm.getStaticData();

        // copy fields which are not saved as part of the OVF
        dstStatic.setExportDate(srcStatic.getExportDate());
        dstStatic.setManagedDeviceMap(srcStatic.getManagedDeviceMap());
        dstStatic.setUnmanagedDeviceList(srcStatic.getUnmanagedDeviceList());
        dstStatic.setOvfVersion(srcStatic.getOvfVersion());

        // Copy creationDate to ignore it, because it is never changed by user.
        // Without this creationDate will always show change in milliseconds,
        // because creationDate is saved without milliseconds in OVF, but
        // with milliseconds in the DB.
        dstStatic.setCreationDate(srcStatic.getCreationDate());

        // Hot plug CPU & memory are displayed separately in the confirmation dialog,
        // so it is not needed to include them into changed fields list.
        if (srcStatic.getCpuPerSocket() == dstStatic.getCpuPerSocket()
                && srcStatic.getNumOfSockets() != dstStatic.getNumOfSockets()) {
            dstStatic.setNumOfSockets(srcStatic.getNumOfSockets());
        }
        // currently only hot plug memory is supported here (no hot unplug)
        if (srcStatic.getMemSizeMb() < dstStatic.getMemSizeMb()) {
            dstStatic.setMemSizeMb(srcStatic.getMemSizeMb());
        }

        VmPropertiesUtils vmPropertiesUtils = SimpleDependecyInjector.getInstance().get(VmPropertiesUtils.class);

        vmPropertiesUtils.separateCustomPropertiesToUserAndPredefined(
                srcVm.getVdsGroupCompatibilityVersion(), srcStatic);
        vmPropertiesUtils.separateCustomPropertiesToUserAndPredefined(
                dstVm.getVdsGroupCompatibilityVersion(), dstStatic);

        Set<String> result = new HashSet<>(VmHandler.getChangedFieldsForStatus(srcStatic, dstStatic, VMStatus.Up));

        for (VmDeviceUpdate device :
                VmHandler.getVmDevicesFieldsToUpdateOnNextRun(srcVm.getId(), VMStatus.Up, getParameters().getUpdateVmParameters())) {
            if (!device.getName().isEmpty()) {
                result.add(device.getName());
            } else {
                switch (device.getType()) {
                    case UNKNOWN:
                    case VIRTIO:
                        result.add(device.getGeneralType().name());
                        break;

                    default:
                        result.add(device.getType().getName());
                        break;
                }
            }
        }

        setReturnValue(new ArrayList<>(result));
    }

}
