package com.daviipkp.smartstevex.services;

import org.springframework.stereotype.Service;
import javax.sound.sampled.*;
import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class VoiceService {

    private static final String CURRENT_DIR = System.getProperty("user.dir");

    private static final String PIPER_FOLDER = CURRENT_DIR + File.separator + "piper";

    private static final String PIPER_EXE = PIPER_FOLDER + File.separator + "piper.exe";

    private static final String MODEL_PATH = PIPER_FOLDER + File.separator + "en_GB-alan-medium.onnx";
    private static final String TEMP_AUDIO_FILE = PIPER_FOLDER + File.separator + "debug_audio.wav";

    private static Thread speakThread;

    private static float volume = 1;

    public static boolean isPiperHere() {
        return new File(PIPER_EXE).exists();
    }

    public static boolean downloadPiper() {
        String DOWNLOAD_URL = "https://cloud.daviipkp.org/s/aPmseGW3KjryEJb/download";
        String ZIP_FILE_PATH = CURRENT_DIR + File.separator + "piper.zip";

        try {
            Files.copy(URI.create(DOWNLOAD_URL).toURL().openStream(), Paths.get(ZIP_FILE_PATH), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            System.out.println("Couldn't download Piper! Check for updates..?");
            return false;
        }

        try {
            unzip(ZIP_FILE_PATH, DOWNLOAD_URL);
        } catch (IOException e) {
            System.out.println("Piper was downloaded successfully, but we were unable to extract it. Check the file piper.zip for corruption!");
            return false;
        }

        try {
            Files.delete(Paths.get(ZIP_FILE_PATH));
        } catch (IOException e){
            System.out.println("Couldn't delete Piper.zip from your folder.");
        }


        return true;
    }

    public static void unzip(String zipFilePath, String destDir) throws IOException {
        File dir = new File(destDir);
        if (!dir.exists()) dir.mkdirs();

        try (ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath))) {
            ZipEntry entry = zipIn.getNextEntry();
            
            while (entry != null) {
                Path filePath = Paths.get(destDir).resolve(entry.getName()).normalize();
                if (!filePath.startsWith(Paths.get(destDir).normalize())) {
                    throw new IOException("Entrada ZIP inválida: " + entry.getName());
                }

                if (entry.isDirectory()) {
                    Files.createDirectories(filePath);
                } else {
                    Files.createDirectories(filePath.getParent());
                    Files.copy(zipIn, filePath, StandardCopyOption.REPLACE_EXISTING);
                }
                zipIn.closeEntry();
                entry = zipIn.getNextEntry();
            }
            zipIn.close();
        }
    }

    public static void speak(String text) {
        shutUp();
        speakThread = new Thread(() -> {
            try {
                generateWavFile(text);
                EarService s = SpringContext.getBean(EarService.class);
                s.stopListening();

                playWavFile();

                s.resumeListening();
                shutUp();
            } catch (Exception e) {
                System.out.println("Error trying to speak (Play LLM response): " + e.getMessage());
            }
        });
        speakThread.start();
    }

    public static void speak(String text, Runnable onFinish) {
        shutUp();
        speakThread = new Thread(() -> {
            try {
                generateWavFile(text);
                EarService s = SpringContext.getBean(EarService.class);
                s.stopListening();

                playWavFile();

                s.resumeListening();
                shutUp();
            } catch (Exception e) {
                System.out.println("Error trying to speak (Play LLM response): " + e.getMessage());
            }
        });
        speakThread.start();
    }

    public static void shutUp() {
        if(speakThread != null) {
            speakThread.interrupt();
        }
    }

    public static Thread getCurrentThread() {
        return speakThread;
    }

    private static void generateWavFile(String text) throws IOException, InterruptedException {
        String safeText = text.replace("\n", " ").replace("\"", "");

        ProcessBuilder pb = new ProcessBuilder(
                PIPER_EXE,
                "--model", MODEL_PATH,
                "--output_file", TEMP_AUDIO_FILE
        );

        pb.directory(new File(PIPER_FOLDER));
        pb.redirectError(ProcessBuilder.Redirect.INHERIT);

        Process process = pb.start();

        try (OutputStream os = process.getOutputStream()) {
            os.write(safeText.getBytes(StandardCharsets.UTF_8));
            os.flush();
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            System.err.println("Error while trying to generate Wav File.");
        } else {
            File f = new File(TEMP_AUDIO_FILE);
        }
    }

    public static void setVolume(float newVolume) {
        if (newVolume < 0f) volume = 0f;
        else volume = Math.min(newVolume, 1f);
    }

    private static void playWavFile() {
        try {
            File audioFile = new File(TEMP_AUDIO_FILE);
            if (!audioFile.exists() || audioFile.length() < 100) return;

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);

            if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

                float dB = (float) (Math.log(volume != 0 ? volume : 0.0001) / Math.log(10.0) * 20.0);
                gainControl.setValue(dB);
            }


            clip.start();
            Thread.sleep(clip.getMicrosecondLength() / 1000);

            clip.close();
            audioStream.close();
        } catch (Exception e) {
            System.out.println("Error trying to play Wav File: " + e.getMessage());
        }
    }
}