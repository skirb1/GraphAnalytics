package FinalProject;

import graph.GraphUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;

import java.io.*;
import java.util.*;

public class DirectedGraph {

    private static final String NETSTAT_DIR =
            "C:\\Users\\kirbysm1\\Documents\\JHU_EP\\GraphAnalytics\\HCP_Data\\BrainGraphData\\directed_network_stats";

    private static final String DIRECTED_GRAPHS_DIR =
            "C:\\Users\\kirbysm1\\Documents\\JHU_EP\\GraphAnalytics\\HCP_Data\\BrainGraphData\\pit_group_connectomes_directed\\directed_graphml";

    private static final String OUTPUT_DIR =
            "C:\\Users\\kirbysm1\\Documents\\JHU_EP\\GraphAnalytics\\HCP_Data\\BrainGraphData\\pit_group_connectomes_directed";

    private static final String ANALYSIS_RESULTS_FILE = "analysis_results_with_path.csv";

    public static final String TOTAL_DEGREE = "total_degree";

    public static final String IN_DEGREE = "in_degree";

    public static final String OUT_DEGREE = "out_degree";

    public static final String NODE_NAME = "dn_name";

    public static final String CLUSTER_COEFF = "cluster_coeff";

    public static final String AVG_PATH_LENGTH = "avg_shortest_path";

    public static void main(String[] args){
        List<String> graphFiles = MyGraphUtils.getGraphFiles(DIRECTED_GRAPHS_DIR);

        for(String file : graphFiles){
            setGraphData(file);
        }

        saveAnalysisResults(graphFiles,
                OUTPUT_DIR + File.separator + ANALYSIS_RESULTS_FILE);
    }

    /**
     * Adds total_degree, in_degree, out_degree, cluster_coeff to all nodes in
     * a graphml file
     * @param file Graohml file to add node properties to
     */
    public static void setGraphData(String file)
    {
        TinkerGraph graph = GraphUtils.readGraphML(file);
        Iterator<Vertex> it = graph.vertices();
        while(it.hasNext())
        {
            Vertex v = it.next();
            v.property(TOTAL_DEGREE, MyUtils.countIterator(v.vertices(Direction.BOTH)));
            v.property(IN_DEGREE, MyUtils.countIterator(v.vertices(Direction.IN)));
            v.property(OUT_DEGREE, MyUtils.countIterator(v.vertices(Direction.OUT)));
            v.property(CLUSTER_COEFF, MyGraphUtils.clusterCoefficientLocal(v));
        }
        GraphUtils.saveGraphML(graph, file);
    }

    /**
     * Averages the total degree, in degree, out degree, and cluster coefficient
     * for each node in a set of graphs and saves results to a csv
     * @param graphFiles The graphml files to iterate over
     * @param outputFile The full path of the output csv file
     */
    public static void saveAnalysisResults(List<String> graphFiles, String outputFile){
        try {
            String[] headers = {"Name", "Avg Total", /*"Avg In", "Avg Out", "Cluster Coeff",*/ "Avg Path"};
            FileWriter out = new FileWriter(outputFile);
            CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT
                    .withHeader(headers));
            TinkerGraph graph = MyGraphUtils.readGraphML(graphFiles.get(0));
            GraphTraversalSource g = graph.traversal();


            Map<String, Double> avgTotal = getIntegerPropertyDistr(
                    graphFiles, TOTAL_DEGREE, true);
            Map<String, Double> avgIn = getIntegerPropertyDistr(
                    graphFiles, IN_DEGREE, true);
            Map<String, Double> avgOut = getIntegerPropertyDistr(
                    graphFiles, OUT_DEGREE, true);
            Map<String, Double> avgCluster = getDoublePropertyDistr(
                    graphFiles, CLUSTER_COEFF, true);

            Map<String, Double> avgPath = getAvgShortestPath(
                    MyGraphUtils.getNetstatFiles(NETSTAT_DIR), true);

            for (String node : avgTotal.keySet()) {
                String id = (String)g.V().has(NODE_NAME, node).id().next();

                printer.printRecord(
                        node, avgTotal.get(node), avgIn.get(node),
                        avgOut.get(node), avgCluster.get(node), avgPath.get(id));
            }

            printer.close();
            out.close();
            System.out.println("Analysis results saved to csv");
        }
        catch(IOException ex){
            System.out.println("Error saving results to csv");
        }
    }

    /**
     * Averages a node property over a set of grapml files, expected to all
     * have the same nodes.
     * @param fileNames Graphml files to iterate over
     * @param property The node property to average
     * @param excludeZeroes If true, zero values are excluded from the averaging
     *                      calculation
     * @return A Map of node names and the average value of the
     *         specified property for that node
     */
    public static Map<String, Double> getIntegerPropertyDistr(
            List<String> fileNames, String property, boolean excludeZeroes){

        Map<String, Double> propertyAvgMap = new HashMap<>();
        Map<String, List<Double>> propertySumMap = new HashMap<>();

        // Collect degree counts from each graph
        for(String file: fileNames){
            TinkerGraph graph = GraphUtils.readGraphML(file);
            Iterator<Vertex> it = graph.vertices();
            while(it.hasNext()) {
                Vertex v = it.next();
                String name = v.property(NODE_NAME).value().toString();
                Double value = ((Integer) v.property(property).value()).doubleValue();
                // don't count nodes with degree 0
                if(value == 0 && excludeZeroes){
                    continue;
                }

                List<Double> degreeList;

                if(propertySumMap.containsKey(name)){
                    degreeList = propertySumMap.get(name);
                    degreeList.add(value);
                }
                else {
                    degreeList = new ArrayList<>();
                    degreeList.add(value);
                    propertySumMap.put(name, degreeList);
                }
            }
        }

        // Calculate average degree count
        for(String node: propertySumMap.keySet()){
            List<Double> values = propertySumMap.get(node);
            propertyAvgMap.put(node, MyUtils.getAverage(values));
        }

        return propertyAvgMap;
    }

    /**
     * Averages a node property over a set of grapml files, expected to all
     * have the same nodes.
     * @param fileNames Graphml files to iterate over
     * @param property The node property to average
     * @param excludeZeroes If true, zero values are excluded from the averaging
     *                      calculation
     * @return A Map of node names and the average value of the
     *         specified property for that node
     */
    public static Map<String, Double> getDoublePropertyDistr(
            List<String> fileNames, String property, boolean excludeZeroes){

        Map<String, Double> propertyAvgMap = new HashMap<>();
        Map<String, List<Double>> propertySumMap = new HashMap<>();

        // Collect degree counts from each graph
        for(String file: fileNames){
            TinkerGraph graph = GraphUtils.readGraphML(file);
            Iterator<Vertex> it = graph.vertices();
            while(it.hasNext()) {
                Vertex v = it.next();
                String name = v.property(NODE_NAME).value().toString();
                Double value = (Double) v.property(property).value();
                // don't count nodes with degree 0
                if(value == 0 && excludeZeroes){
                    continue;
                }

                List<Double> degreeList;

                if(propertySumMap.containsKey(name)){
                    degreeList = propertySumMap.get(name);
                    degreeList.add(value);
                }
                else {
                    degreeList = new ArrayList<>();
                    degreeList.add(value);
                    propertySumMap.put(name, degreeList);
                }
            }
        }

        // Calculate average degree count
        for(String node: propertySumMap.keySet()){
            List<Double> values = propertySumMap.get(node);
            propertyAvgMap.put(node, MyUtils.getAverage(values));
        }

        return propertyAvgMap;
    }

    public static Map<String, Double> getAvgShortestPath(List<String> fileNames, boolean excludeZeroes){
        Map<String, Double> propertyAvgMap = new HashMap<>();
        Map<String, List<Double>> propertySumMap = new HashMap<>();

        // Collect degree counts from each graph
        for(String file: fileNames){
            try {
                BufferedReader buf = new BufferedReader(new FileReader(file));
                String line = null;

                buf.readLine();//skip first line
                while (true) {
                    line = buf.readLine();
                    if(line == null){
                        break;
                    }

                    String[] attributes = line.split("\t");
                    String nodeID = attributes[0];
                    Double avgPath = Double.parseDouble(attributes[12]);

                    // don't count nodes with degree 0
                    if (avgPath == 0 && excludeZeroes) {
                        continue;
                    }

                    List<Double> degreeList;
                    if (propertySumMap.containsKey(nodeID)) {
                        degreeList = propertySumMap.get(nodeID);
                        degreeList.add(avgPath);
                    } else {
                        degreeList = new ArrayList<>();
                        degreeList.add(avgPath);
                        propertySumMap.put(nodeID, degreeList);
                    }
                }
            }
            catch(IOException ex){
                System.out.println("Error reading network statistics file");
            }
        }

        // Calculate average degree count
        for(String node: propertySumMap.keySet()){
            List<Double> values = propertySumMap.get(node);
            propertyAvgMap.put(node, MyUtils.getAverage(values));
        }

        return propertyAvgMap;
    }

}
