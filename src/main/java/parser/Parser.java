package parser;
//Yusuf Buğra KILIÇ- B231202355
//Kayra Kaan KABAKÇIOĞLU- B231202350
//Dilay Gülru ÖZAK- B231202371
import java.io.*;
import java.util.*;

public class Parser {

    // ActionTable ve GotoTable tablolarını ve grammer kurallarını tutan yapılar
    private Map<Integer, Map<String, String>> actionTable = new HashMap<>();
    private Map<Integer, Map<String, Integer>> gotoTable = new HashMap<>();
    private Map<Integer, Rule> grammar = new HashMap<>();
    // Gramer kuralını temsil eden iç sınıf

    static class Rule {
        String lhs;  // Sol taraf (non-terminal)
        List<String> rhs; // Sağ taraf (semboller listesi)
        Rule(String lhs, List<String> rhs) {
            this.lhs = lhs;
            this.rhs = rhs;
        }
    }
    // Parse ağacının düğümlerini temsil eden iç sınıf
    static class TreeNode {
        String value;
        List<TreeNode> children = new ArrayList<>();

        TreeNode(String value) {
            this.value = value;
        }
    }
    // Constructor: dosya yollarını alıp Action, Goto ve grammer tablolarını yükler
    public Parser(String actionFile, String gotoFile, String grammarFile) throws IOException {
        loadActionTable(actionFile);
        loadGotoTable(gotoFile);
        loadGrammar(grammarFile);
    }
//ActionTable ı reusources dan yükler
    private void loadActionTable(String file) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                getClass().getClassLoader().getResourceAsStream(file)))) {
            String[] headers = br.readLine().trim().split("\\s+");
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.trim().split("\\s+");
                int state = Integer.parseInt(parts[0]);
                Map<String, String> row = new HashMap<>();
                for (int i = 1; i < parts.length; i++) {
                    if (!parts[i].equals("-")) {
                        row.put(headers[i], parts[i]);
                    }
                }
                actionTable.put(state, row);
            }
        }
    }
//GotoTable ı reusources dan yükler

    private void loadGotoTable(String file) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                getClass().getClassLoader().getResourceAsStream(file)))) {
            String[] headers = br.readLine().trim().split("\\s+");
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.trim().split("\\s+");
                int state = Integer.parseInt(parts[0]);
                Map<String, Integer> row = new HashMap<>();
                for (int i = 1; i < parts.length; i++) {
                    if (!parts[i].equals("-")) {
                        row.put(headers[i], Integer.parseInt(parts[i]));
                    }
                }
                gotoTable.put(state, row);
            }
        }
    }
//Grammar ı resources dan yükler
    private void loadGrammar(String file) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                getClass().getClassLoader().getResourceAsStream(file)))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.trim().split("\\s+", 2);
                int index = Integer.parseInt(parts[0]);
                String[] ruleParts = parts[1].split("->");
                String lhs = ruleParts[0].trim();
                List<String> rhs = Arrays.asList(ruleParts[1].trim().split("\\s+"));
                grammar.put(index, new Rule(lhs, rhs));
            }
        }
    }
    // Parsing işlemi yaplırı
    public List<String> parse(List<String> tokens) {
        List<String> output = new ArrayList<>();
        Stack<Integer> stateStack = new Stack<>();
        Stack<String> symbolStack = new Stack<>();
        Stack<TreeNode> treeStack = new Stack<>();

        // Giriş $ ile bitmiyorsa sona $ ekler
        if (tokens.isEmpty() || !tokens.get(tokens.size() - 1).equals("$")) {
            tokens.add("$");
        }

        stateStack.push(0);
        int index = 0;

        // Tablo başlıkları
        output.add(String.format("%-40s %-40s %-40s", "Stack", "Input", "Action"));
        output.add("-".repeat(120));

        TreeNode root = null;

        // Parsing döngüsü
        while (true) {
            int state = stateStack.peek();
            String token = tokens.get(index);
            String action = actionTable.getOrDefault(state, Map.of()).get(token);

            String stackStr = buildStack(stateStack, symbolStack);
            String inputStr = String.join(" ", tokens.subList(index, tokens.size()));

            if (action == null) {
                // Geçerli Action bulunamadıysa syntax error
                output.add(String.format("%-40s %-40s %-40s", stackStr, inputStr, "Syntax Error"));
                break;
            }

            if (action.startsWith("s")) {
                // SHIFT işlemi: yeni duruma geç
                int next = Integer.parseInt(action.substring(1));
                stateStack.push(next);
                symbolStack.push(token);
                treeStack.push(new TreeNode(token));
                output.add(String.format("%-40s %-40s %-40s", stackStr, inputStr, "Shift " + next));
                index++;

            } else if (action.startsWith("r")) {
                // REDUCE işlemi: kurala göre indirgeme yap
                int ruleNum = Integer.parseInt(action.substring(1));
                Rule rule = grammar.get(ruleNum);
                List<TreeNode> children = new ArrayList<>();

                // Sağ taraftaki semboller kadar stackten çıkar
                for (int i = 0; i < rule.rhs.size(); i++) {
                    symbolStack.pop();
                    stateStack.pop();
                    children.add(treeStack.pop());
                }
                // Childları sıraya koy ve yeni tree node oluştur
                Collections.reverse(children);
                TreeNode node = new TreeNode(rule.lhs);
                node.children.addAll(children);
                treeStack.push(node);

                // Yeni non-terminal ve durum hesaplanır
                symbolStack.push(rule.lhs);
                int go = gotoTable.get(stateStack.peek()).get(rule.lhs);
                stateStack.push(go);

                output.add(String.format("%-40s %-40s %-40s", stackStr, inputStr,
                        "Reduce " + ruleNum + " (GOTO [" + stateStack.get(stateStack.size() - 2) + ", " + rule.lhs + "])"));

                root = node;
            } else if (action.equals("accept")) {
                output.add(String.format("%-40s %-40s %-40s", stackStr, inputStr, "ACCEPTED"));
                break;
            }
        }

        output.add("-".repeat(120));
        output.add("Parse tree:");
        // Parse ağacı yazdırılır
        List<String> treeLines = new ArrayList<>();
        if (root != null) {
            printTree(root, new ArrayList<>(), treeLines);
        }
        output.addAll(treeLines);
        return output;
    }

    private void printTree(TreeNode node, List<String> path, List<String> output) {
        path.add(node.value);
        output.add("/" + String.join("/", path));
        for (TreeNode child : node.children) {
            printTree(child, new ArrayList<>(path), output);
        }
    }
    // Stack görünümünü string olarak üretir
    private String buildStack(Stack<Integer> states, Stack<String> symbols) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < symbols.size(); i++) {
            sb.append(states.get(i)).append(symbols.get(i));
        }
        sb.append(states.peek());
        return sb.toString();
    }
}