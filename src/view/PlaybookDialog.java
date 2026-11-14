package view;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

/** Displays the assignment-specific rules as a paged, book-style guide. */
final class PlaybookDialog extends JDialog {
    private static final Color COVER = new Color(78, 47, 30);
    private static final Color PAPER = new Color(255, 249, 230);
    private static final Color INK = new Color(56, 42, 31);
    private static final Color HEADER = new Color(20, 82, 52);

    private final List<PlaybookPage> pages = createPages();
    private final PagePanel leftPage = new PagePanel();
    private final PagePanel rightPage = new PagePanel();
    private final JLabel pageIndicator = new JLabel();
    private final JButton previousButton = new JButton("< Previous");
    private final JButton nextButton = new JButton("Next >");
    private int firstPageIndex;

    PlaybookDialog(Frame owner) {
        super(owner, "Sorry! Playbook", true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setContentPane(createContent());
        installKeyboardNavigation();
        showSpread(0);
        setSize(1000, 700);
        setMinimumSize(new Dimension(860, 620));
        setLocationRelativeTo(owner);

        URL iconUrl = getClass().getResource("/image/icon.png");
        if (iconUrl != null) {
            setIconImage(new ImageIcon(iconUrl).getImage());
        }
    }

    private JComponent createContent() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(HEADER);

        JPanel heading = new JPanel(new BorderLayout(12, 0));
        heading.setBackground(HEADER);
        heading.setBorder(new EmptyBorder(10, 24, 8, 24));

        JLabel icon = new JLabel(loadScaledIcon("/image/icon.png", 42, 42));
        JLabel title = new JLabel("SORRY! PLAYBOOK");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        JLabel hint = new JLabel("Assignment rules - use Left/Right arrows to turn pages");
        hint.setForeground(new Color(220, 239, 226));
        hint.setFont(new Font("SansSerif", Font.PLAIN, 12));

        JPanel titleBlock = new JPanel(new BorderLayout());
        titleBlock.setOpaque(false);
        titleBlock.add(title, BorderLayout.CENTER);
        titleBlock.add(hint, BorderLayout.SOUTH);
        heading.add(icon, BorderLayout.WEST);
        heading.add(titleBlock, BorderLayout.CENTER);

        BookPanel book = new BookPanel();
        book.add(leftPage);
        book.add(rightPage);

        JPanel navigation = new JPanel(new BorderLayout(14, 0));
        navigation.setBackground(HEADER);
        navigation.setBorder(new EmptyBorder(8, 24, 12, 24));
        styleNavigationButton(previousButton);
        styleNavigationButton(nextButton);
        previousButton.addActionListener(event -> showSpread(firstPageIndex - 2));
        nextButton.addActionListener(event -> showSpread(firstPageIndex + 2));
        pageIndicator.setForeground(Color.WHITE);
        pageIndicator.setHorizontalAlignment(SwingConstants.CENTER);
        pageIndicator.setFont(new Font("SansSerif", Font.BOLD, 13));
        navigation.add(previousButton, BorderLayout.WEST);
        navigation.add(pageIndicator, BorderLayout.CENTER);
        navigation.add(nextButton, BorderLayout.EAST);

        root.add(heading, BorderLayout.NORTH);
        root.add(book, BorderLayout.CENTER);
        root.add(navigation, BorderLayout.SOUTH);
        return root;
    }

    private void styleNavigationButton(JButton button) {
        button.setBackground(new Color(250, 229, 166));
        button.setForeground(INK);
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        button.setPreferredSize(new Dimension(130, 34));
    }

    private void showSpread(int requestedIndex) {
        int lastSpread = Math.max(0, ((pages.size() - 1) / 2) * 2);
        firstPageIndex = Math.max(0, Math.min(requestedIndex, lastSpread));
        leftPage.showPage(pages.get(firstPageIndex), firstPageIndex + 1);
        if (firstPageIndex + 1 < pages.size()) {
            rightPage.showPage(pages.get(firstPageIndex + 1), firstPageIndex + 2);
        } else {
            rightPage.showEndPage();
        }

        int visibleLastPage = Math.min(firstPageIndex + 2, pages.size());
        pageIndicator.setText("Pages " + (firstPageIndex + 1) + "-"
                + visibleLastPage + " of " + pages.size());
        previousButton.setEnabled(firstPageIndex > 0);
        nextButton.setEnabled(firstPageIndex + 2 < pages.size());
    }

    private void installKeyboardNavigation() {
        JComponent root = getRootPane();
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "previousPage");
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "nextPage");
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_HOME, 0), "firstPage");
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_END, 0), "lastPage");
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "closePlaybook");

        root.getActionMap().put("previousPage", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent event) {
                showSpread(firstPageIndex - 2);
            }
        });
        root.getActionMap().put("nextPage", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent event) {
                showSpread(firstPageIndex + 2);
            }
        });
        root.getActionMap().put("firstPage", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent event) {
                showSpread(0);
            }
        });
        root.getActionMap().put("lastPage", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent event) {
                showSpread(pages.size() - 1);
            }
        });
        root.getActionMap().put("closePlaybook", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent event) {
                dispose();
            }
        });
    }

    private static List<PlaybookPage> createPages() {
        return Arrays.asList(
                page("Welcome to Sorry!",
                        "Move both of your pawns from Start, clockwise around the board, "
                                + "through your own Safety zone, and into Home. The first player "
                                + "to bring both pawns Home wins.<br><br>"
                                + "This guide follows the supplied HY252 assignment rules, which "
                                + "have a few deliberate differences from the commercial game.",
                        "/image/playbook/board-overview.png"),
                page("A player's turn",
                        "<ol><li>Draw the top card.</li>"
                                + "<li>Apply its movement if a legal move exists.</li>"
                                + "<li>If no legal move exists, use Fold. Card 11 may also be "
                                + "folded instead of swapping.</li>"
                                + "<li>The used card leaves the deck. When all 44 cards are used, "
                                + "the deck is shuffled again.</li></ol>"
                                + "Card 2 grants another turn, including after consecutive 2s."),
                page("Start and your pawns",
                        "Each player has two pawns in a matching Start area. A pawn leaves Start "
                                + "only with card 1 or 2, landing on the coloured entry square. "
                                + "Pawns normally move clockwise. They may pass over other pawns, "
                                + "but cannot finish on a square occupied by their own pawn.",
                        "/image/playbook/start-yellow.png",
                        "/image/playbook/start-red.png"),
                page("Captures and occupied squares",
                        "If your pawn finishes on an opponent's pawn, the opponent returns to "
                                + "Start and your pawn takes that square. Two pawns never share a "
                                + "normal square. Start and Home are the only shared destinations "
                                + "for a player's own pawns.",
                        "/image/redPawn1.png",
                        "/image/yellowPawn1.png"),
                page("Slides",
                        "Landing exactly on the triangle at the start of another colour's slide "
                                + "moves your pawn to its circular end. Every pawn on that slide - "
                                + "even one of your own - returns to its Start. Your own colour's "
                                + "slide does not activate. Landing in the middle does not activate it.",
                        "/image/playbook/slide-short.png",
                        "/image/playbook/slide-long.png"),
                page("Safety zone",
                        "Only your pawns may enter your five-square Safety zone. Entry must be "
                                + "made while moving forward; a backward move cannot cross into "
                                + "Safety. Pawns inside Safety are protected from opponents, swaps, "
                                + "and Sorry! replacements.",
                        "/image/playbook/safety-red.png",
                        "/image/playbook/safety-yellow.png"),
                page("Home and winning",
                        "Home is beyond your Safety zone. A pawn must arrive with the exact card "
                                + "value - it cannot move past Home. Once Home, that pawn never moves "
                                + "again. Bring both of your pawns Home to win the game.",
                        "/image/playbook/home-yellow.png",
                        "/image/playbook/home-red.png"),
                page("Cards 1, 2, and 3",
                        "<b>1:</b> Leave Start or move one pawn forward 1.<br>"
                                + "<b>2:</b> Leave Start or move one pawn forward 2, then play again.<br>"
                                + "<b>3:</b> Move every pawn that can legally move forward 3. If only "
                                + "one pawn can move, move that pawn.",
                        "/image/playbook/card-1.jpg",
                        "/image/playbook/card-2.jpg",
                        "/image/playbook/card-3.jpg"),
                page("Cards 4, 5, and 7",
                        "<b>4:</b> Move one pawn backward 4.<br>"
                                + "<b>5:</b> Move every pawn that can legally move forward 5.<br>"
                                + "<b>7:</b> Move one pawn forward 7, or split all 7 forward steps "
                                + "between both pawns. Card 7 cannot leave Start and cannot move backward.",
                        "/image/playbook/card-4.jpg",
                        "/image/playbook/card-5.jpg",
                        "/image/playbook/card-7.jpg"),
                page("Cards 8 and 10",
                        "<b>8:</b> Move one pawn forward 8, or draw another card.<br>"
                                + "<b>10:</b> Move one pawn forward 10 or backward 1. If forward 10 "
                                + "is impossible but backward 1 is legal, the backward move is required.",
                        "/image/playbook/card-8.jpg",
                        "/image/playbook/card-10.jpg"),
                page("Cards 11 and 12",
                        "<b>11:</b> Move forward 11 or optionally swap with an opponent on an "
                                + "eligible outer-track square. Start, Safety, and Home cannot be "
                                + "used in a swap. You may Fold instead of swapping.<br>"
                                + "<b>12:</b> Move forward 12, or draw another card.",
                        "/image/playbook/card-11.jpg",
                        "/image/playbook/card-12.jpg"),
                page("The Sorry! card",
                        "Move one of your pawns from Start onto an eligible outer-track opponent. "
                                + "The opponent returns to Start. Safety and Home cannot be targeted. "
                                + "Without a pawn in Start or a legal target, discard the card.<br><br>"
                                + "<b>Keys:</b> Left/Right turn pages, Home/End jump, Esc closes.",
                        "/image/playbook/card-sorry.jpg",
                        "/image/playbook/start-yellow.png",
                        "/image/playbook/start-red.png"));
    }

    private static PlaybookPage page(String title, String body, String... images) {
        return new PlaybookPage(title, body, images);
    }

    private static ImageIcon loadScaledIcon(String resource, int width, int height) {
        URL url = PlaybookDialog.class.getResource(resource);
        if (url == null) {
            return new ImageIcon();
        }
        Image image = new ImageIcon(url).getImage().getScaledInstance(
                width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(image);
    }

    private static final class PlaybookPage {
        private final String title;
        private final String body;
        private final String[] images;

        private PlaybookPage(String title, String body, String[] images) {
            this.title = title;
            this.body = body;
            this.images = images;
        }
    }

    private static final class BookPanel extends JPanel {
        private BookPanel() {
            super(new GridLayout(1, 2, 16, 0));
            setOpaque(false);
            setBorder(new EmptyBorder(18, 26, 18, 26));
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D graphics2D = (Graphics2D) graphics.create();
            graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            graphics2D.setColor(COVER);
            graphics2D.fillRoundRect(8, 7, getWidth() - 16, getHeight() - 14, 28, 28);
            graphics2D.setColor(new Color(45, 28, 20));
            graphics2D.setStroke(new BasicStroke(5));
            graphics2D.drawLine(getWidth() / 2, 22,
                    getWidth() / 2, getHeight() - 22);
            graphics2D.dispose();
            super.paintComponent(graphics);
        }
    }

    private static final class PagePanel extends JPanel {
        private final JLabel title = new JLabel("", SwingConstants.CENTER);
        private final JPanel imageGrid = new JPanel();
        private final JEditorPane text = new JEditorPane();

        private PagePanel() {
            super(new BorderLayout(10, 10));
            setBackground(PAPER);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(205, 184, 142), 1),
                    new EmptyBorder(16, 18, 12, 18)));

            title.setForeground(INK);
            title.setFont(new Font("Serif", Font.BOLD, 22));
            title.setBorder(new EmptyBorder(0, 0, 5, 0));

            imageGrid.setOpaque(false);
            imageGrid.setPreferredSize(new Dimension(390, 245));

            text.setContentType("text/html");
            text.setEditable(false);
            text.setOpaque(false);
            text.setBorder(null);
            text.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
            text.setFont(new Font("SansSerif", Font.PLAIN, 13));
            JScrollPane textScroll = new JScrollPane(text);
            textScroll.setBorder(null);
            textScroll.setOpaque(false);
            textScroll.getViewport().setOpaque(false);
            textScroll.setPreferredSize(new Dimension(390, 180));
            textScroll.getVerticalScrollBar().setUnitIncrement(12);

            add(title, BorderLayout.NORTH);
            add(imageGrid, BorderLayout.CENTER);
            add(textScroll, BorderLayout.SOUTH);
        }

        private void showPage(PlaybookPage page, int pageNumber) {
            title.setText(page.title);
            imageGrid.removeAll();
            int imageCount = page.images.length;
            int rows = imageCount > 3 ? 2 : 1;
            imageGrid.setLayout(new GridLayout(rows, 0, 8, 8));
            if (imageCount == 0) {
                imageGrid.add(new JLabel(loadScaledIcon("/image/sorryImage.png", 271, 77),
                        SwingConstants.CENTER));
            } else {
                for (String imageResource : page.images) {
                    imageGrid.add(new ScaledImagePanel(imageResource));
                }
            }
            text.setText("<html><body style='font-family:sans-serif; font-size:12px; "
                    + "color:#382a1f; margin:4px;'>" + page.body
                    + "<div style='text-align:right; margin-top:8px;'><i>Page "
                    + pageNumber + "</i></div></body></html>");
            text.setCaretPosition(0);
            revalidate();
            repaint();
        }

        private void showEndPage() {
            title.setText("End of playbook");
            imageGrid.removeAll();
            imageGrid.setLayout(new BorderLayout());
            imageGrid.add(new JLabel(loadScaledIcon("/image/sorryImage.png", 271, 77),
                    SwingConstants.CENTER));
            text.setText("<html><body style='font-family:sans-serif; font-size:13px; "
                    + "text-align:center; color:#382a1f;'>Good luck!</body></html>");
            revalidate();
            repaint();
        }
    }

    private static final class ScaledImagePanel extends JPanel {
        private final Image image;

        private ScaledImagePanel(String resource) {
            setOpaque(false);
            URL url = PlaybookDialog.class.getResource(resource);
            if (url == null) {
                throw new IllegalStateException("Missing playbook image: " + resource);
            }
            image = new ImageIcon(url).getImage();
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            int sourceWidth = image.getWidth(this);
            int sourceHeight = image.getHeight(this);
            if (sourceWidth <= 0 || sourceHeight <= 0) {
                return;
            }

            double scale = Math.min((double) getWidth() / sourceWidth,
                    (double) getHeight() / sourceHeight);
            int width = Math.max(1, (int) Math.round(sourceWidth * scale));
            int height = Math.max(1, (int) Math.round(sourceHeight * scale));
            int x = (getWidth() - width) / 2;
            int y = (getHeight() - height) / 2;
            Graphics2D graphics2D = (Graphics2D) graphics.create();
            graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            graphics2D.drawImage(image, x, y, width, height, this);
            graphics2D.dispose();
        }
    }
}
