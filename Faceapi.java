import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.List;
import javax.imageio.*;
import com.microsoft.azure.cognitiveservices.vision.face.*;
import com.microsoft.azure.cognitiveservices.vision.face.models.*;
import javax.imageio.ImageIO;

public class Faceapi extends JFrame {
    private static final String subscriptionKey = "71e704b01f9e43d9b104fb30b4a9400f";
    private static final String faceEndpoint = "https://ti2faceapi.cognitiveservices.azure.com/";
    private final IFaceClient faceClient = FaceClient.authenticate(subscriptionKey).withEndpoint(faceEndpoint);
    private static final String defaultStatusBarText = "Hover the mouse to see details.";
    private String[] faceDescriptions;
    private List<DetectedFace> faceList;
    private double resizeFactor;

    private JLabel facePhoto;
    private JLabel description;

    public FaceAnalysisDemo() {
        setTitle("Face Analysis Demo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        facePhoto = new JLabel();
        description = new JLabel(defaultStatusBarText);

        JButton btnBrowse = new JButton("Browse");
        btnBrowse.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                browseButtonClicked();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnBrowse);

        JPanel imagePanel = new JPanel();
        imagePanel.setLayout(new BorderLayout());
        imagePanel.add(facePhoto, BorderLayout.CENTER);

        JPanel statusPanel = new JPanel();
        statusPanel.add(description);

        add(buttonPanel, BorderLayout.NORTH);
        add(imagePanel, BorderLayout.CENTER);
        add(statusPanel, BorderLayout.SOUTH);
    }

    private void browseButtonClicked() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("JPEG Image", "jpg"));

        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();
            File imageFile = new File(filePath);

            try {
                BufferedImage image = ImageIO.read(imageFile);
                ImageIcon imageIcon = new ImageIcon(image);
                facePhoto.setIcon(imageIcon);
                setTitle("Detecting faces...");
                faceList = uploadAndDetectFaces(imageFile);
                setTitle(faceList.size() + " face(s) detected");

                if (faceList.size() > 0) {
                    // Draw rectangles around the faces.
                    BufferedImage imageWithRect = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g2d = imageWithRect.createGraphics();
                    g2d.drawImage(image, 0, 0, null);
                    double dpi = image.getDPI();
                    resizeFactor = (dpi == 0) ? 1 : 96 / dpi;
                    faceDescriptions = new String[faceList.size()];

                    for (int i = 0; i < faceList.size(); ++i) {
                        DetectedFace face = faceList.get(i);

                        // Draw a rectangle on the face.
                        g2d.setColor(Color.RED);
                        g2d.setStroke(new BasicStroke(2));
                        FaceRectangle fr = face.faceRectangle();
                        g2d.drawRect((int)(fr.left() * resizeFactor), (int)(fr.top() * resizeFactor), (int)(fr.width() * resizeFactor), (int)(fr.height() * resizeFactor));

                        // Store the face description.
                        faceDescriptions[i] = faceDescription(face);
                    }

                    g2d.dispose();
                    facePhoto.setIcon(new ImageIcon(imageWithRect));
                    description.setText(defaultStatusBarText);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private List<DetectedFace> uploadAndDetectFaces(File imageFile) {
        // The list of Face attributes to return.
        List<FaceAttributeType> faceAttributes = Arrays.asList(
            FaceAttributeType.GENDER,
            FaceAttributeType.EMOTION,
            FaceAttributeType.SMILE,
            FaceAttributeType.GLASSES
        );

        // Call the Face API.
        try {
            byte[] imageBytes = Files.readAllBytes(imageFile.toPath());
            InputStream imageStream = new ByteArrayInputStream(imageBytes);

            return faceClient.face().detectWithStream(imageStream, true, false, faceAttributes);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private String faceDescription(DetectedFace face) {
        StringBuilder sb = new StringBuilder();

        // Add the gender, age, and smile.
        sb.append("Gender: ").append(face.faceAttributes().gender()).append("\n");
        sb.append(String.format("Smile %.1f%% ", face.faceAttributes().smile() * 100));

        // Add glasses.
        sb.append("\n").append(face.faceAttributes().glasses()).append("\n");

        // Add the emotions. Display all emotions over 10%.
        sb.append("Emotion: ");
        Emotion emotionScore = face.faceAttributes().emotion();
        if (emotionScore.anger() >= 0.1f) sb.append(String.format("anger %.1f%%, ", emotionScore.anger() * 100));
        if (emotionScore.contempt() >= 0.1f) sb.append(String.format("contempt %.1f%%, ", emotionScore.contempt() * 100));
        if (emotionScore.disgust() >= 0.1f) sb.append(String.format("disgust %.1f%%, ", emotionScore.disgust() * 100));
        if (emotionScore.fear() >= 0.1f) sb.append(String.format("fear %.1f%%, ", emotionScore.fear() * 100));
        if (emotionScore.happiness() >= 0.1f) sb.append(String.format("happiness %.1f%%, ", emotionScore.happiness() * 100));
        if (emotionScore.neutral() >= 0.1f) sb.append(String.format("neutral %.1f%%, ", emotionScore.neutral() * 100));
        if (emotionScore.sadness() >= 0.1f) sb.append(String.format("sadness %.1f%%, ", emotionScore.sadness() * 100));
        if (emotionScore.surprise() >= 0.1f) sb.append(String.format("surprise %.1f%% ", emotionScore.surprise() * 100));

        return sb.toString();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                FaceAnalysisDemo demo = new FaceAnalysisDemo();
                demo.setVisible(true);
            }
        });
    }
}
