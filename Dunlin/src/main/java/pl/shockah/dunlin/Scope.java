package pl.shockah.dunlin;

public abstract class Scope {
    public Scope downscope() {
        return null;
    }

    public abstract String name();
}