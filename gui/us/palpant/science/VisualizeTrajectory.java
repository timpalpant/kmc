package us.palpant.science;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JProgressBar;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import us.palpant.science.io.Frame;
import us.palpant.science.io.TrajectoryReader;

public class VisualizeTrajectory {

  private JFrame frame = new JFrame();
  JProgressBar progressBar = new JProgressBar();
  private LatticePanel latticePanel;
  private TrajectoryReader reader;
  private boolean finished = false;
  private Frame nextFrame;
  private double t = 0;
  
  @Parameter(names = { "-i", "--input" }, description = "Input file with trajetory", 
      converter = PathConverter.class, validateWith = ReadablePathValidator.class)
  public Path inputFile;
  @Parameter(names = { "-t", "--time" }, description = "Length of time to simulate", required = true)
  public double tFinal;

  /**
   * Launch the application.
   * @throws IOException 
   */
  public static void main(String[] args) throws IOException {
    final VisualizeTrajectory app = new VisualizeTrajectory();
    // Initialize the command-line options parser
    JCommander jc = new JCommander(app);
    jc.setProgramName("VisualizeTrajectory");

    try {
      jc.parse(args);
    } catch (ParameterException e) {
      System.err.println(e.getMessage());
      jc.usage();
      System.exit(-1);
    }
    
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          app.initialize();
          app.frame.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  /**
   * Initialize the contents of the frame.
   * 
   * @throws IOException
   */
  private void initialize() throws IOException {
    frame.setBounds(100, 100, 800, 100);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    progressBar.setMaximum((int) (100*tFinal));
    frame.getContentPane().add(progressBar, BorderLayout.SOUTH);

    reader = new TrajectoryReader(inputFile);
    try {
      nextFrame = reader.readFrame();
    } catch (ClassNotFoundException e1) {
      finished = true;
      e1.printStackTrace();
    }
    
    latticePanel = new LatticePanel(reader.getLatticeSize());
    frame.getContentPane().add(latticePanel, BorderLayout.CENTER);

    new javax.swing.Timer(30, new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        loop();
      }
    }).start();
  }

  private void loop() {
    if (!finished) {
      t += tFinal / 2000;
      progressBar.setValue((int) (100*t));
      if (t >= tFinal) {
        finished = true;
        progressBar.setValue(progressBar.getMaximum());
      }

      if (nextFrame.getTime() < t) {
        int[] positions = nextFrame.getPositions();
        latticePanel.setPositions(positions);
        
        while (nextFrame != null && nextFrame.getTime() < t) {
          try {
            nextFrame = reader.readFrame();
          } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            finished = true;
          }
        }

        if (nextFrame == null) {
          finished = true;
          progressBar.setValue(progressBar.getMaximum());
        }
      }
    }
  }
}
