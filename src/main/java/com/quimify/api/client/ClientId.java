package com.quimify.api.client;

import java.io.Serializable;
import java.util.Objects;

class ClientId implements Serializable {

    String platform;
    Integer version;

    @Override
    public int hashCode() {
        return Objects.hash(platform, version);
    }

    @Override
    public boolean equals(Object other) {
        if (other == null || getClass() != other.getClass())
            return false;

        ClientId otherClientId = (ClientId) other;

        return Objects.equals(platform, otherClientId.platform) && Objects.equals(version, otherClientId.version);
    }

}
