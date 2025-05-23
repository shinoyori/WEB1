package br.ufscar.dc.dsw.domain.enums;

public enum Role {
    GUEST(0),
    TESTER(1),
    ADMIN(2);

    private final int nivel;

    Role(int nivel) {
        this.nivel = nivel;
    }

    public boolean hasAccess(Role minimo) {
        return this.nivel >= minimo.nivel;
    }
}

