package com.x7ubi.indexcards.response.image;

import java.util.UUID;

public class ImageResponse {

    private UUID imageId;

    public ImageResponse() {}

    public ImageResponse(UUID imageId) {
        this.imageId = imageId;
    }

    public UUID getImageId() {
        return imageId;
    }

    public void setImageId(UUID imageId) {
        this.imageId = imageId;
    }
}
