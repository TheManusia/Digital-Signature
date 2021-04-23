package xyz.themanusia.digitalsignature.ui.motionview.model;

import androidx.annotation.FloatRange;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Layer {
    float x;
    float y;
    float scale;
    @FloatRange(from = 0.0f, to = 360.0f)
    float rotationInDegrees;
    boolean isFlipped;

    public Layer() {
        reset();
    }

    private void reset() {
        this.rotationInDegrees = 0.0f;
        this.x = 0.0f;
        this.y = 0.0f;
        this.scale = 1.0f;
        this.isFlipped = false;
    }

    public void postScale(float scaleDiff) {
        float newVal = scale + scaleDiff;
        if (newVal >= getMinScale() && newVal <= getMaxScale()) {
            scale = newVal;
        }
    }

    protected float getMaxScale() {
        return Limits.MAX_SCALE;
    }

    protected float getMinScale() {
        return Limits.MIN_SCALE;
    }

    public void postRotate(float rotationInDegreesDiff) {
        this.rotationInDegrees += rotationInDegreesDiff;
        this.rotationInDegrees %= 360.0F;
    }

    public void postTranslate(float dx, float dy) {
        this.x += dx;
        this.y += dy;
    }

    public void flip() {
        this.isFlipped = !isFlipped;
    }

    public float initialScale() {
        return Limits.INITIAL_ENTITY_SCALE;
    }

    public float getRotationInDegrees() {
        return rotationInDegrees;
    }

    public void setRotationInDegrees(@FloatRange(from = 0.0, to = 360.0) float rotationInDegrees) {
        this.rotationInDegrees = rotationInDegrees;
    }

    interface Limits {
        float MIN_SCALE = 0.06F;
        float MAX_SCALE = 4.0F;
        float INITIAL_ENTITY_SCALE = 0.4F;
    }
}
