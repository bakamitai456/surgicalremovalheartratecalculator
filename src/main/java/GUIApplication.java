import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class GUIApplication {

    private static File selectedFile;
    private static List<Double> heartRatePerMinDataList;
    private static Map<Integer, List<Double>> groupData;
    private static JTextArea textArea;
    private static JButton calculateButton;
    private static JButton selectButton;
    private static JTextField filePathField;

    public static void main(String[] args) {
        //Create and set up the window.
        JFrame frame = new JFrame("HelloWorldSwing");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        BorderLayout mainLayout = new BorderLayout();
        frame.setLayout(mainLayout);

        //Add the ubiquitous "Hello World" label.
        Container pane = frame.getContentPane();

        textArea = new JTextArea("Select the file before click calculate.");
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        pane.add(scrollPane, BorderLayout.CENTER);
        filePathField = new JTextField();
        filePathField.setEditable(false);
        selectButton = new JButton("Select File");
        calculateButton = new JButton("Calculate");
        JPanel buttonPanel = new JPanel(new GridLayout(3, 1));
        buttonPanel.add(filePathField);
        buttonPanel.add(selectButton);
        buttonPanel.add(calculateButton);
        pane.add(buttonPanel,BorderLayout.PAGE_END);

        selectButton.addActionListener(e -> selectFileAction());

        calculateButton.addActionListener(e -> calculateAction());

        //Display the window.
        frame.pack();
        frame.setSize(600, 800);
        frame.setVisible(true);
    }

    private static void calculateAction(){
        textArea.setText("");
        BufferedReader reader = null;
        if(selectedFile != null && selectedFile.exists()) {
            try {
                reader = new BufferedReader(new FileReader(selectedFile));
                String groupPosData = reader.readLine();

                heartRatePerMinDataList = getHeartRateData(reader);
                groupData = new HashMap<>();

                createGroupDataByStartPos(groupPosData, heartRatePerMinDataList, groupData);
                textArea.append(getGroupDataText(groupData));

                List<String> highestGroupList = new LinkedList<>();
                Double highestNumber = 0.0;

                highestNumber = findHighest(groupData, highestGroupList, highestNumber);
                textArea.append(getHighestNumberAndGroupText(highestGroupList, highestNumber));
                textArea.append("Average: " + getAverage().toString());

            } catch (FileNotFoundException e) {
                textArea.append("Program Error. Hint: file is not found.");
                textArea.append(e.getStackTrace().toString());
            } catch (IOException e) {
                textArea.append("Program Error. Copy text below this and search stackoverflow.");
                textArea.append(e.getStackTrace().toString());
            }
        }else{
            textArea.setText("Select the file before click calculate.");
        }
    }

    private static Double getAverage(){
        Double sum = 0.0;
        for(Double data: heartRatePerMinDataList){
            sum += data;
        }
        return sum/heartRatePerMinDataList.size();
    }

    private static void selectFileAction(){
        JFileChooser chooser = new JFileChooser();
        int returnVal = chooser.showOpenDialog(null);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            selectedFile = chooser.getSelectedFile();
            filePathField.setText(selectedFile.getAbsolutePath());
        }
    }

    private static String getHighestNumberAndGroupText(java.util.List<String> highestGroupList, Double highestNumber) {
        StringBuilder builder = new StringBuilder();
        builder.append("Highest Number: ").append(highestNumber).append("\n").append("In Group: ").append(String.join(",", highestGroupList)).append("\n");

        return builder.toString();
    }

    private static Double findHighest(Map<Integer, List<Double>> groupData, List<String> highestGroupList, Double highestNumber) {
        for(int i=0;i<groupData.size();i++){
            java.util.List<Double> dataList = groupData.get(i);
            for(Double num: dataList){
                if(num >= highestNumber){
                    if(num > highestNumber) {
                        highestGroupList.clear();
                        highestGroupList.add(i + "");
                    }
                    highestNumber = num;
                }
            }
        }
        return highestNumber;
    }

    private static void createGroupDataByStartPos(String groupPosData, java.util.List<Double> heartRatePerMinDataList, Map<Integer, java.util.List<Double>> groupData) {
        StringTokenizer tokenizer = new StringTokenizer(groupPosData, ",");
        int groupCount = 0;
        int startIndex = Integer.parseInt(tokenizer.nextToken())-1;
        int endIndex;
        do {
            if(tokenizer.hasMoreTokens()) {
                endIndex = Integer.parseInt(tokenizer.nextToken())-1;
            }else{
                endIndex = heartRatePerMinDataList.size();
            }
            java.util.List<Double> groupDataList = heartRatePerMinDataList.subList(startIndex, endIndex);
            groupData.put(groupCount, groupDataList);
            groupCount++;
            startIndex = endIndex;
        } while(tokenizer.hasMoreTokens());
        endIndex = heartRatePerMinDataList.size();
        java.util.List<Double> groupDataList = heartRatePerMinDataList.subList(startIndex, endIndex);
        groupData.put(groupCount, groupDataList);
    }

    private static String getGroupDataText(Map<Integer, java.util.List<Double>> groupData) {
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i< groupData.size(); i++){
            java.util.List<Double> dataList = groupData.get(i);
            builder.append("Group Data " + i + ":\n");
            for(Double num: dataList){
                builder.append(num + " ");
            }
            builder.append("\n");
        }
        return builder.toString();
    }

    private static java.util.List<Double> getHeartRateData(BufferedReader reader) throws IOException {
        String fullDataLine;
        List<Double> dataList = new ArrayList<Double>();
        while((fullDataLine = reader.readLine())!= null && fullDataLine.length() > 0){
            StringTokenizer tokenizer = new StringTokenizer(fullDataLine, ",");
            Double[] dataLine = new Double[2];
            int dataPosCount = 0;
            while (tokenizer.hasMoreTokens()){
                dataLine[dataPosCount] = Double.parseDouble(tokenizer.nextToken());
                dataPosCount++;
            }

            Double avg = findDoubleAverage(dataLine);
            dataList.add(avg);
        }
        return dataList;
    }

    private static Double findDoubleAverage(Double[] dataLine) {
        Double sum = 0.0;
        for(Double num: dataLine){
            sum += num;
        }
        return sum/dataLine.length;
    }

}
