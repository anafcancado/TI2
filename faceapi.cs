using System;
using System.Collections.Generic;
using System.IO;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using Microsoft.Azure.CognitiveServices.Vision.Face;
using Microsoft.Azure.CognitiveServices.Vision.Face.Models;

namespace FaceAnalysisDemo
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {
        private static string subscriptionKey = "71e704b01f9e43d9b104fb30b4a9400f";
        private static string faceEndpoint = "https://ti2faceapi.cognitiveservices.azure.com/";
        private readonly IFaceClient faceClient = new FaceClient(new ApiKeyServiceClientCredentials(subscriptionKey), new System.Net.Http.DelegatingHandler[] { });
        private const string defaultStatusBarText = "Hover the mouse to see details.";
        private string[] faceDescriptions;
        private IList<DetectedFace> faceList;
        private double resizeFactor;

        public MainWindow()
        {
            InitializeComponent();
            faceClient.Endpoint = faceEndpoint;
        }

        private async void btnBrowse_Click(object sender, RoutedEventArgs e)
        {
            var openDlg = new Microsoft.Win32.OpenFileDialog();
            openDlg.Filter = "JPEG Image(*.jpg)|*.jpg";
            bool? result = openDlg.ShowDialog(this);

            if (!(bool)result)
            {
                return;
            }

            // Display the image file.
            string filePath = openDlg.FileName;

            Uri fileUri = new Uri(filePath);
            BitmapImage bitmapSource = new BitmapImage();

            bitmapSource.BeginInit();
            bitmapSource.CacheOption = BitmapCacheOption.None;
            bitmapSource.UriSource = fileUri;
            bitmapSource.EndInit();

            FacePhoto.Source = bitmapSource;
            Title = "Detecting faces...";
            faceList = await UploadAndDetectFaces(filePath);
            //Title = $"Detection activity finished. {faceList.Count} face(s) detected";
            Title = $"{faceList.Count} face(s) detected";
            if (faceList.Count > 0)
            {
                // Draw rectangles around the faces.
                DrawingVisual visual = new DrawingVisual();
                DrawingContext drawingContext = visual.RenderOpen();
                drawingContext.DrawImage(bitmapSource, new Rect(0, 0, bitmapSource.Width, bitmapSource.Height));
                double dpi = bitmapSource.DpiX;
                resizeFactor = (dpi == 0) ? 1 : 96 / dpi; // some images don't contain DPI info 
                faceDescriptions = new String[faceList.Count];

                for (int i = 0; i < faceList.Count; ++i)
                {
                    DetectedFace face = faceList[i];

                    // Draw a rectangle on the face.
                    drawingContext.DrawRectangle(
                        Brushes.Transparent,
                        new Pen(Brushes.Red, 2),
                        new Rect(
                            face.FaceRectangle.Left * resizeFactor,
                            face.FaceRectangle.Top * resizeFactor,
                            face.FaceRectangle.Width * resizeFactor,
                            face.FaceRectangle.Height * resizeFactor
                            )
                    );

                    // Store the face description.
                    faceDescriptions[i] = FaceDescription(face);
                }

                drawingContext.Close();

                // Display the image with rectangle 
                RenderTargetBitmap faceWithRectBitmap = new RenderTargetBitmap(
                    (int)(bitmapSource.PixelWidth * resizeFactor),
                    (int)(bitmapSource.PixelHeight * resizeFactor),
                    96,
                    96,
                    PixelFormats.Pbgra32);

                faceWithRectBitmap.Render(visual);
                FacePhoto.Source = faceWithRectBitmap;

                // Set the status bar text.
                description.Text = defaultStatusBarText;
            }
        }

        private void FacePhoto_MouseMove(object sender, MouseEventArgs e)
        {
            if (faceList == null)
                return;

            // Find the mouse position with respect to image.
            Point mouseXY = e.GetPosition(FacePhoto);

            ImageSource imageSource = FacePhoto.Source;
            BitmapSource bitmapSource = (BitmapSource)imageSource;

            // Scale adjustment between the actual size and displayed size.
            var scale = FacePhoto.ActualWidth / (bitmapSource.PixelWidth / resizeFactor);

            // Check if this mouse position is over a face rectangle.
            bool mouseOverFace = false;

            for (int i = 0; i < faceList.Count; ++i)
            {
                FaceRectangle fr = faceList[i].FaceRectangle;
                double left = fr.Left * scale;
                double top = fr.Top * scale;
                double width = fr.Width * scale;
                double height = fr.Height * scale;

                // Display description only if the mouse is over rectangle.
                if (mouseXY.X >= left && mouseXY.X <= left + width &&
                    mouseXY.Y >= top && mouseXY.Y <= top + height)
                {
                    description.Text = faceDescriptions[i];
                    mouseOverFace = true;
                    break;
                }
            }

            // default text when the mouse is not over a face rectangle.
            if (!mouseOverFace) description.Text = defaultStatusBarText;
        }

        private async Task<IList<DetectedFace>> UploadAndDetectFaces(string imageFilePath)
        {
            // The list of Face attributes to return.
            IList<FaceAttributeType> faceAttributes =
                new FaceAttributeType[]
                {
                    FaceAttributeType.Gender,
                    FaceAttributeType.Emotion,
                    FaceAttributeType.Smile,
                    FaceAttributeType.Glasses
                };

            // Call the Face API.
            try
            {
                using (Stream imageFileStream = File.OpenRead(imageFilePath))
                {
                    IList<DetectedFace> faceList = await faceClient.Face.DetectWithStreamAsync(imageFileStream, true, false, faceAttributes);
                    return faceList;
                }
            }           
            catch (APIErrorException f)
            {
                MessageBox.Show(f.Message);
                return new List<DetectedFace>();
            }      
            catch (Exception e)
            {
                MessageBox.Show(e.Message, "Error occurred");
                return new List<DetectedFace>();
            }
        }

        // Creates a string out of the attributes describing the face.
        private string FaceDescription(DetectedFace face)
        {
            StringBuilder sb = new StringBuilder();

            // Add the gender, age, and smile.
            sb.Append($"Gender: {face.FaceAttributes.Gender}\n");
            sb.Append(String.Format("Smile {0:F1}% ", face.FaceAttributes.Smile * 100));

            // Add glasses.
            sb.Append($"\n{face.FaceAttributes.Glasses}\n");

            // Add the emotions. Display all emotions over 10%.
            sb.Append("Emotion: ");
            Emotion emotionScore = face.FaceAttributes.Emotion;
            if (emotionScore.Anger >= 0.1f) sb.Append(
                String.Format("anger {0:F1}%, ", emotionScore.Anger * 100));
            if (emotionScore.Contempt >= 0.1f) sb.Append(
                String.Format("contempt {0:F1}%, ", emotionScore.Contempt * 100));
            if (emotionScore.Disgust >= 0.1f) sb.Append(
                String.Format("disgust {0:F1}%, ", emotionScore.Disgust * 100));
            if (emotionScore.Fear >= 0.1f) sb.Append(
                String.Format("fear {0:F1}%, ", emotionScore.Fear * 100));
            if (emotionScore.Happiness >= 0.1f) sb.Append(
                String.Format("happiness {0:F1}%, ", emotionScore.Happiness * 100));
            if (emotionScore.Neutral >= 0.1f) sb.Append(
                String.Format("neutral {0:F1}%, ", emotionScore.Neutral * 100));
            if (emotionScore.Sadness >= 0.1f) sb.Append(
                String.Format("sadness {0:F1}%, ", emotionScore.Sadness * 100));
            if (emotionScore.Surprise >= 0.1f) sb.Append(
                String.Format("surprise {0:F1}% ", emotionScore.Surprise * 100));
            return sb.ToString();
        }
    }
}