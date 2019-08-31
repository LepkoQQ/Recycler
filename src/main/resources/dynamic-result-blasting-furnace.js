var ASMAPI = Java.type("net.minecraftforge.coremod.api.ASMAPI");
var Opcodes = Java.type("org.objectweb.asm.Opcodes");
var InsnList = Java.type("org.objectweb.asm.tree.InsnList");
var AbstractInsnNode = Java.type("org.objectweb.asm.tree.AbstractInsnNode");
var InsnNode = Java.type("org.objectweb.asm.tree.InsnNode");
var VarInsnNode = Java.type("org.objectweb.asm.tree.VarInsnNode");
var FieldInsnNode = Java.type("org.objectweb.asm.tree.FieldInsnNode");
var MethodInsnNode = Java.type("org.objectweb.asm.tree.MethodInsnNode");
var LabelNode = Java.type("org.objectweb.asm.tree.LabelNode");
var JumpInsnNode = Java.type("org.objectweb.asm.tree.JumpInsnNode");

function findFirstLabel(methodNode) {
    var instructionsLength = methodNode.instructions.size();
    for (var i = 0; i < instructionsLength; i++) {
        var instruction = methodNode.instructions.get(i);
        if (instruction.getType() == AbstractInsnNode.LABEL) {
            return instruction;
        }
    }
    throw "Error: Couldn't find first label!";
}

function insertInstructions(methodNode, afterInstruction, instructions) {
    var insnList = new InsnList();
    var instructionsLength = instructions.length;
    for (var i = 0; i < instructionsLength; i++) {
        insnList.add(instructions[i]);
    }
    methodNode.instructions.insert(afterInstruction, insnList);
}

// Main function
function initializeCoreMod() {
    return {
        "TRANSFORM1": {
            "target": {
                "type": "METHOD",
                "class": "net.minecraft.tileentity.AbstractFurnaceTileEntity",
                "methodName": "func_214008_b", // canSmelt
                "methodDesc": "(Lnet/minecraft/item/crafting/IRecipe;)Z"
            },
            "transformer": function (methodNode) {
                // label that marks the start of original instructions
                var originalInstructionsLabel = new LabelNode();

                insertInstructions(methodNode, findFirstLabel(methodNode), [
                    // prepare arguments
                    new VarInsnNode(Opcodes.ALOAD, 0),  // ARG 1, this
                    new VarInsnNode(Opcodes.ALOAD, 1),  // ARG 2, recipe
                    new VarInsnNode(Opcodes.ALOAD, 0),  // ARG 3, this (for next GETFIELD)
                    new FieldInsnNode(                  // ARG 3, this.items
                        Opcodes.GETFIELD,
                        "net/minecraft/tileentity/AbstractFurnaceTileEntity",
                        ASMAPI.mapField("field_214012_a"), // items
                        "Lnet/minecraft/util/NonNullList;"
                    ),
                    // call method
                    new MethodInsnNode(
                        Opcodes.INVOKESTATIC,
                        "ga/poglej/recycler/Hooks",
                        "canSmelt",
                        "(Lnet/minecraft/tileentity/AbstractFurnaceTileEntity;Lnet/minecraft/item/crafting/IRecipe;Lnet/minecraft/util/NonNullList;)I",
                        false // isInterface
                    ),
                    // duplicate value on top of the stack (returned value from method call)
                    new InsnNode(Opcodes.DUP),
                    // if value on top of the stack is less than 0 jump
                    new JumpInsnNode(Opcodes.IFLT, originalInstructionsLabel),
                    // return value on top of the stack from the method
                    new InsnNode(Opcodes.IRETURN),
                    // label everything after this as the original instructions
                    originalInstructionsLabel
                ]);

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
                // label that marks the start of original instructions
                var originalInstructionsLabel = new LabelNode();

                insertInstructions(methodNode, findFirstLabel(methodNode), [
                    // prepare arguments
                    new VarInsnNode(Opcodes.ALOAD, 0),  // ARG 1, this
                    new VarInsnNode(Opcodes.ALOAD, 1),  // ARG 2, recipe
                    new VarInsnNode(Opcodes.ALOAD, 0),  // ARG 3, this (for next GETFIELD)
                    new FieldInsnNode(                  // ARG 3, this.items
                        Opcodes.GETFIELD,
                        "net/minecraft/tileentity/AbstractFurnaceTileEntity",
                        ASMAPI.mapField("field_214012_a"), // items
                        "Lnet/minecraft/util/NonNullList;"
                    ),
                    // call method
                    new MethodInsnNode(
                        Opcodes.INVOKESTATIC,
                        "ga/poglej/recycler/Hooks",
                        "finishSmelting",
                        "(Lnet/minecraft/tileentity/AbstractFurnaceTileEntity;Lnet/minecraft/item/crafting/IRecipe;Lnet/minecraft/util/NonNullList;)Z",
                        false // isInterface
                    ),
                    // if value on top of the stack is 0 (false) jump
                    new JumpInsnNode(Opcodes.IFEQ, originalInstructionsLabel),
                    // return from method
                    new InsnNode(Opcodes.RETURN),
                    // label everything after this as the original instructions
                    originalInstructionsLabel
                ]);

                return methodNode;
            }
        },
    };
}
