package com.github.unidbg;

import com.alibaba.fastjson.util.IOUtils;
import com.github.unidbg.arm.backend.Unicorn2Factory;
import com.github.unidbg.linux.android.AndroidEmulatorBuilder;
import com.github.unidbg.linux.android.AndroidResolver;
import com.github.unidbg.linux.android.dvm.*;
import com.github.unidbg.memory.Memory;

import java.io.File;

public class Sign extends AbstractJni {
    private final AndroidEmulator emulator;
    private final VM vm;
    private final DvmClass Utils;

    Sign() {

        emulator = AndroidEmulatorBuilder.for64Bit()
                .setProcessName("com.qidian.dldl.official")
                .addBackendFactory(new Unicorn2Factory(true))
                .build(); // 创建模拟器实例，要模拟32位或者64位，在这里区分
        final Memory memory = emulator.getMemory(); // 模拟器的内存操作接口
        memory.setLibraryResolver(new AndroidResolver(23)); // 设置系统类库解析
        vm = emulator.createDalvikVM(); // 创建Android虚拟机
        vm.setJni(this);
        vm.setVerbose(false);
        DalvikModule dm = vm.loadLibrary(new File("src/test/resources/example_binaries/libwtf.so"), false); // 加载so到unicorn虚拟内存，加载成功以后会默认调用init_array等函数
        dm.callJNI_OnLoad(emulator); // 手动执行JNI_OnLoad函数
        Utils = vm.resolveClass("cn/thecover/lib/common/manager/SignManager");

    }

    @Override
    public DvmObject<?> callStaticObjectMethodV(BaseVM vm, DvmClass dvmClass, String signature, VaList vaList) {
        if (signature.equals("cn/thecover/lib/common/utils/LogShutDown->getAppSign()Ljava/lang/String;")) {
            return new StringObject(vm, "1");
        }
        return super.callStaticObjectMethodV(vm, dvmClass, signature, vaList);
    }

    void destroy() {
        IOUtils.close(emulator);
    }

    String getSign(String account, String token, String valueOf) {
        StringObject array = Utils.callStaticJniMethodObject(emulator,
                "getSign(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;",
                new StringObject(vm, account), new StringObject(vm, token), new StringObject(vm, valueOf)); // 执行Jni方法
        return array.getValue();
    }

    public static void main(String[] args) throws Exception {
        Sign test = new Sign();
        String result = test.getSign("", "", "");
        System.out.println(result);
        test.destroy();
    }

}
