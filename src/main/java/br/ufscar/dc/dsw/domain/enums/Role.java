package br.ufscar.dc.dsw.domain.enums;

public enum Role {
    GUEST(0),
    USER(1),
    ADMIN(2);

    private final int nivel;

    Role(int nivel) {
        this.nivel = nivel;
    }

    public boolean hasAccess(Role minimo) {
        return this.nivel >= minimo.nivel;
    }
}

