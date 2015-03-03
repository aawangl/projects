import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class GUI {
    private static JFrame _mainFrame;
    private static JPanel _dotPanel;
    private static JPanel _buttonPanel;
    private static JLabel _messageLabel;
    private static JLabel _scoreLabel = new JLabel("Score: 0");
    private static JLabel _movesLabel = new JLabel("Moves Left: " + 0);
    private static HashMap<Integer, String> _colorMap;
    private static Board _currBoard;
    private static int _size;
    private static JButton[][] _dotButtons;

    public static void initGUI(int n) {
        _mainFrame = new JFrame();
        _mainFrame.setLayout(null);
        _mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        _mainFrame.setSize(800, 600);
        _mainFrame.setVisible(true);
        _mainFrame.setTitle("DOTS");
        _colorMap = new HashMap<Integer, String>();
        _colorMap.put(Dot.COLOR_RED, "images/redDot.png");
        _colorMap.put(Dot.COLOR_BLUE, "images/blueDot.png");
        _colorMap.put(Dot.COLOR_GREEN, "images/greenDot.png");
        _colorMap.put(Dot.COLOR_YELLOW, "images/yellowDot.png");
        _colorMap.put(Dot.COLOR_PURPLE, "images/purpleDot.png");
        _colorMap.put(-Dot.COLOR_RED, "images/redDoti.png");
        _colorMap.put(-Dot.COLOR_BLUE, "images/blueDoti.png");
        _colorMap.put(-Dot.COLOR_GREEN, "images/greenDoti.png");
        _colorMap.put(-Dot.COLOR_YELLOW, "images/yellowDoti.png");
        _colorMap.put(-Dot.COLOR_PURPLE, "images/purpleDoti.png");
        _dotPanel = new JPanel();

        _size = n;
        _dotButtons = new JButton[n][n];
        _buttonPanel = new JPanel();
        _buttonPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        JButton button = new JButton("Remove");
        button.addActionListener(new Remove());
        c.gridx = 0;
        c.gridy = 0;
        _buttonPanel.add(button, c);
        c.gridy = 1;
        button = new JButton("Best Square");
        button.addActionListener(new DoAI());
        _buttonPanel.add(button, c);
        
        // keep this separate from _buttonPanel
        JButton button2 = new JButton("New Game");
        button2.setBounds(rect(500, 300, button.getPreferredSize()));
        button2.addActionListener(new doNewGame());
        _mainFrame.add(button2);
        
        _buttonPanel.setBounds(rect(500, 200, _buttonPanel.getPreferredSize()));
        _mainFrame.add(_buttonPanel);
        _messageLabel = new JLabel(
                "this is long text that is just meant to adjust the \n"
                        + "preferred size of the message label. just ignore it as it will be \n"
                        + "replaced by nothing.\n\n");
        _messageLabel
                .setBounds(rect(450, 250, _messageLabel.getPreferredSize()));
        _messageLabel.setText("");

        _scoreLabel = new JLabel("Score: 9999999999");
        _scoreLabel.setBounds(rect(475, 150, _scoreLabel.getPreferredSize()));
        _scoreLabel.setText("Score: 0");

        _movesLabel = new JLabel("Moves: 9999999999");
        _movesLabel.setBounds(rect(475, 165, _movesLabel.getPreferredSize()));
        _movesLabel.setText("Moves: " + Board.getMovesAllowed());

        _scoreLabel.setVisible(true);
        _messageLabel.setVisible(true);
        _movesLabel.setVisible(true);

        _scoreLabel.repaint();
        _messageLabel.repaint();
        _movesLabel.repaint();

        _mainFrame.add(_messageLabel);
        _mainFrame.add(_scoreLabel);
        _mainFrame.add(_movesLabel);

        _mainFrame.repaint();
        _mainFrame.validate();
    }

    
    public static void fillBoard() {
        _messageLabel.setText("");
        if (_currBoard.getMovesLeft() == 0) {
            gameOver();
        }
        _scoreLabel.setText("Score: " + _currBoard.getScore());
        _movesLabel.setText("Moves: " + _currBoard.getMovesLeft());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        _dotPanel.removeAll();
        _dotPanel.setLayout(new GridBagLayout());
        JButton dot;
        for (int i = 0; i < _size; i++) {
            for (int j = 0; j < _size; j++) {
                int color = _currBoard.getBoard()[i][j].getColor();
                dot = dotButton(color);
                c.gridx = i;
                c.gridy = _size - j - 1;
                dot.addActionListener(new Select(dot, color, i, j));
                _dotButtons[i][j] = dot;
                _dotPanel.add(dot, c);
                int len = dot.getIcon().getIconHeight();
                dot.setPreferredSize(new Dimension(len, len));
            }
        }
        _dotPanel.setBounds(rect(0, 0, _dotPanel.getPreferredSize()));
        _dotPanel.validate();
        _mainFrame.add(_dotPanel);
        _mainFrame.validate();

        if (_currBoard.isGameOver()) {
            gameOver();
        }
    }

    private static Rectangle rect(int x, int y, Dimension size) {
        Rectangle result = new Rectangle(new Point(x, y), size);
        return result;
    }

    private static JButton dotButton(int colorID) {
        JButton button = new JButton(new ImageIcon(_colorMap.get(colorID)));
        button.setBorder(null);
        return button;
    }

    private static class Select implements ActionListener {
        private int _color;
        private final int _x;
        private final int _y;
        private boolean _inverted = false;
        private final JButton _dot;

        public Select(JButton dot, int color, int x, int y) {
            _color = color;
            _x = x;
            _y = y;
            _dot = dot;
        }

        @Override
        public void actionPerformed(ActionEvent arg0) {
        	if (_inverted && _currBoard.canDeselect(_x, _y)) {
        		_currBoard.deselectDot(_x, _y);
        		invertColor();
        		return;
        	}
            if (!_currBoard.canMakeMove()) {
                gameOver();
                return;
            }
            if (!_currBoard.canSelect(_x, _y)) {
                return;
            }
            _messageLabel.setText("");
            _currBoard.selectDot(_x, _y);
            invertColor();
        }

        private void invertColor() {
            _color *= -1;
            _inverted = !_inverted;
            _dot.setIcon(new ImageIcon(_colorMap.get(_color)));
        }
    }

    private static class Remove implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                _currBoard.removeSelectedDots();
            } catch (Board.CantRemoveException E) {
                _messageLabel.setText("Please select two or more dots.");
                return;
            }
            _currBoard.dropRemainingDots();
            _currBoard.fillRemovedDots();
            fillBoard();
            _scoreLabel.setText("Score: " + _currBoard.getScore());
            _movesLabel.setText("Moves: " + _currBoard.getMovesLeft());
            _mainFrame.validate();
        }
    }

    private static class DoAI implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            ArrayList<Point> square = _currBoard.findBestSquare();
                                    if (square == null) {
                _messageLabel.setText("No squares found.");
                return;
            }
            if (_currBoard.numberSelected() > 0) {
            	_messageLabel.setText("Please deselect any highlighted dots first.");
                return;
            }
            for (Point p : square) {
                _dotButtons[p.x][p.y].doClick();
            }
        }
    }


    private static class doNewGame implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
        	_currBoard = new Board(_size);
            _buttonPanel.setVisible(true);
            _mainFrame.validate();
            fillBoard();
        }
    }
    
    private static void gameOver() {
    	_buttonPanel.setVisible(false);
        _messageLabel.setText("Game Over");
        _mainFrame.validate();
        _mainFrame.repaint();
    }
    
    
}
