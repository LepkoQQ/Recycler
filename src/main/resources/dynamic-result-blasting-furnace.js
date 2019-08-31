function initializeCoreMod() {
    var ASMAPI = Java.type("net.minecraftforge.coremod.api.ASMAPI");
    var AbstractInsnNode = Java.type("org.objectweb.asm.tree.AbstractInsnNode");
    var Opcodes = Java.type("org.objectweb.asm.Opcodes");
    var InsnList = Java.type("org.objectweb.asm.tree.InsnList");
    var VarInsnNode = Java.type("org.objectweb.asm.tree.VarInsnNode");
    var MethodInsnNode = Java.type("org.objectweb.asm.tree.MethodInsnNode");
    var InsnNode = Java.type("org.objectweb.asm.tree.InsnNode");
    var LabelNode = Java.type("org.objectweb.asm.tree.LabelNode");
    var JumpInsnNode = Java.type("org.objectweb.asm.tree.JumpInsnNode");
    var FieldInsnNode = Java.type("org.objectweb.asm.tree.FieldInsnNode");

    return {
        "TRANSFORM1": {
            "target": {
                "type": "METHOD",
                "class": "net.minecraft.tileentity.AbstractFurnaceTileEntity",
                "methodName": "func_214008_b",
                "methodDesc": "(Lnet/minecraft/item/crafting/IRecipe;)Z"
            },
            "transformer": function (methodNode) {
                var firstLabel;
                var instructionsLength = methodNode.instructions.size();
                for (var i = 0; i < instructionsLength; ++i) {
                    var instruction = methodNode.instructions.get(i);
                    if (instruction.getType() == AbstractInsnNode.LABEL) {
                        firstLabel = instruction;
                        break;
                    }
                }

                if (!firstLabel) {
                    throw "Error: Couldn't find first label!";
                }

                // --------------------------------------------------------
                var toInject = new InsnList();
                // ARG 1, get this
                toInject.add(new VarInsnNode(Opcodes.ALOAD, 0)); // this
                // ARG 2, get first arg
                toInject.add(new VarInsnNode(Opcodes.ALOAD, 1)); // IRecipe recipe
                // ARG 3, get field on this
                toInject.add(new VarInsnNode(Opcodes.ALOAD, 0)); // this
                toInject.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/tileentity/AbstractFurnaceTileEntity", ASMAPI.mapField("field_214012_a"), "Lnet/minecraft/util/NonNullList;")); // this.items
                // CALL METHOD
                toInject.add(new MethodInsnNode(
                    // int opcode
                    Opcodes.INVOKESTATIC,
                    // String owner
                    "ga/poglej/recycler/Hooks",
                    // String name
                    "canSmelt",
                    // String descriptor
                    "(Lnet/minecraft/tileentity/AbstractFurnaceTileEntity;Lnet/minecraft/item/crafting/IRecipe;Lnet/minecraft/util/NonNullList;)I",
                    // boolean isInterface
                    false
                ));

                // label that marks the original instructions
                var originalInstructionsLabel = new LabelNode();

                // duplicate value on top of the stack since the next if will consume it and we need to return it after
                toInject.add(new InsnNode(Opcodes.DUP));

                // if prev instruction is less than 0 (-1) jump to original instructions
                toInject.add(new JumpInsnNode(Opcodes.IFLT, originalInstructionsLabel));

                // return the result of the method
                toInject.add(new InsnNode(Opcodes.IRETURN));

                // mark everything after this as the original instructions
                toInject.add(originalInstructionsLabel);

                // insert after first label
                methodNode.instructions.insert(firstLabel, toInject);
                // --------------------------------------------------------

                return methodNode;
            }
        },
        "TRANSFORM0": {
            "target": {
                "type": "METHOD",
                "class": "net.minecraft.tileentity.AbstractFurnaceTileEntity",
                "methodName": "func_214007_c",
                "methodDesc": "(Lnet/minecraft/item/crafting/IRecipe;)V"
            },
            "transformer": function (methodNode) {
                var firstLabel;
                var instructionsLength = methodNode.instructions.size();
                for (var i = 0; i < instructionsLength; ++i) {
                    var instruction = methodNode.instructions.get(i);
                    if (instruction.getType() == AbstractInsnNode.LABEL) {
                        firstLabel = instruction;
                        break;
                    }
                }

                if (!firstLabel) {
                    throw "Error: Couldn't find first label!";
                }

                // --------------------------------------------------------
                var toInject = new InsnList();
                // ARG 1, get this
                toInject.add(new VarInsnNode(Opcodes.ALOAD, 0)); // this
                // ARG 2, get first arg
                toInject.add(new VarInsnNode(Opcodes.ALOAD, 1)); // IRecipe recipe
                // ARG 3, get field on this
                toInject.add(new VarInsnNode(Opcodes.ALOAD, 0)); // this
                toInject.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/tileentity/AbstractFurnaceTileEntity", ASMAPI.mapField("field_214012_a"), "Lnet/minecraft/util/NonNullList;")); // this.items
                // CALL METHOD
                toInject.add(new MethodInsnNode(
                    // int opcode
                    Opcodes.INVOKESTATIC,
                    // String owner
                    "ga/poglej/recycler/Hooks",
                    // String name
                    "finishSmelting",
                    // String descriptor
                    "(Lnet/minecraft/tileentity/AbstractFurnaceTileEntity;Lnet/minecraft/item/crafting/IRecipe;Lnet/minecraft/util/NonNullList;)Z",
                    // boolean isInterface
                    false
                ));

                // label that marks the original instructions
                var originalInstructionsLabel = new LabelNode();

                // if prev instruction is 0 (false) jump to original instructions
                toInject.add(new JumpInsnNode(Opcodes.IFEQ, originalInstructionsLabel));

                // return from method
                toInject.add(new InsnNode(Opcodes.RETURN));

                // mark everything after this as the original instructions
                toInject.add(originalInstructionsLabel);

                // insert after first label
                methodNode.instructions.insert(firstLabel, toInject);
                // --------------------------------------------------------

                return methodNode;
            }
        },
    };
}