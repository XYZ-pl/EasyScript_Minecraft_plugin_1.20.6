package pl.macie.easyscript.api;

public final class EasyScriptApi {
    private static final EasyScriptRegistry REGISTRY = new EasyScriptRegistry();

    private EasyScriptApi() {
    }

    public static EasyScriptRegistry registry() {
        return REGISTRY;
    }

    public static void registerAddon(EasyScriptAddon addon) {
        addon.register(REGISTRY);
    }
}
