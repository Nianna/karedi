package main.java.com.github.nianna.karedi.controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import org.springframework.stereotype.Component;

@Component
public class ScratchpadController implements Controller {
	@FXML
	private AnchorPane pane;
	@FXML
	private TextArea textArea;

	@Override
	public Node getContent() {
		return pane;
	}

	@FXML
	private void consumeKeyPressedEvent(KeyEvent event) {
		event.consume();
	}

}
