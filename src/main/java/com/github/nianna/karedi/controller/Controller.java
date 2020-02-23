package main.java.com.github.nianna.karedi.controller;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.scene.Node;

public interface Controller {
    Node getContent();

    default void onSceneAndContextInitialized() {
    }

    default boolean isDisabled() {
		return getContent().isDisabled();
	}

    default void setDisable(boolean value) {
		getContent().setDisable(value);
	}

    default Node getFocusableContent() {
		return getContent();
	}

    default boolean isFocused() {
		return getFocusableContent().isFocused();
	}

    default void requestFocus() {
		getFocusableContent().requestFocus();
	}

    default ReadOnlyBooleanProperty focusedProperty() {
		return getFocusableContent().focusedProperty();
	}

}
