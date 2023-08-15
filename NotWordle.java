package prog07;

import prog02.GUI;
import prog02.UserInterface;
import prog05.ArrayQueue;
import java.util.PriorityQueue;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class NotWordle {
    UserInterface ui; // class variable

    List<Node> wordEntries = new ArrayList<Node>();

    NotWordle(UserInterface ui) { // constructor that takes a UserInterface
        this.ui = ui; // and stores it in a class variable
    }

    public static void main(String[] args) {
        String start, end;
        GUI ui = new GUI("NotWordle Game ");
        NotWordle game = new NotWordle(ui);
        String fileName = ui.getInfo("What is the file name?");
        while (game.loadWords(fileName) == false) {
            fileName = ui.getInfo("Enter file name");
        }

        //ASK START
        start = ui.getInfo("What is the starting word?");

        //ASK FINAL
        end = ui.getInfo("What is the final word?");

        //ASK HUMAN OR COMPUTER
        String[] commands = { "Human plays", "Solve 1", "Solve 2", "Solve 3" };
        int a = ui.getCommand(commands);
        switch (a) {
            case 0:
                game.play(start, end);
                break;
            case 1:
                game.solve(start, end);
                break;
            case 2:
                game.solve2(start, end);
                break;
            case 3:
                game.solve3(start, end);
                break;
        }

    }

    boolean loadWords(String fileName) {
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(fileName));
            String line;
            Node lineNode;

            while ((line = reader.readLine()) != null) {
                lineNode = new Node(line);
                wordEntries.add(lineNode);
            }

            reader.close();
            return true;
        }

        catch (IOException e) {
            ui.sendMessage("Uh oh: " + e);
            return false;
        }

    }

    void play(String start, String end) {
        //In play, do the following forever (until the return occurs).
        // Tell the user the current word (the start) and the target word.
        //   Ask for the next word.  Set the start word variable to that next word.  If
        //   it equals the target, tell the user ``You win!'' and return.
        //   Otherwise keep looping.  Test.

        while(true) {
            String possibleWord = "";
            ui.sendMessage("The current word is " + start + ". The target word is " + end);
            possibleWord = ui.getInfo("Enter the next word");

            if (possibleWord == null){
                break;
            }

            else if (!oneLetterDifferent(start, possibleWord)) {
                ui.sendMessage("You cannot use " + possibleWord + " because it differs by more than one letter");
                continue;

            }
            else if (find(possibleWord) == null) {
                ui.sendMessage(possibleWord + " is not a word.");
                continue;

            }
            else if (possibleWord.equals(end)) {
                ui.sendMessage("You win!");
                return;

            }
            else {
                start = possibleWord;
            }

        }
    }

    void solve(String start, String end) {
        Queue<Node> queue = new ArrayQueue<>();

        Node startNode = find(start);
        queue.offer(startNode);

        int numPolls = 0;


        while (!queue.isEmpty()) {
            Node theNode = queue.poll();
            numPolls++;

            for (Node nextNode : wordEntries) {
                if (nextNode != startNode && nextNode.next == null && oneLetterDifferent(theNode.word, nextNode.word)) {
                    nextNode.next = theNode;
                    queue.offer(nextNode);
                    if (nextNode.word.equals(end)) {
                        String s = "";
                        for (Node node = nextNode; node != null; node = node.next) {
                            s = node.word + "\n" + s;
                        }
                        ui.sendMessage(s);
                        ui.sendMessage("This took " + numPolls + " polls.");
                        return;
                    }
                }
            }
        }
    }

    void solve2(String start, String end) {
        Queue<Node> queue = new PriorityQueue<>(new NodeComparator(end));

        Node startNode = find(start);
        queue.offer(startNode);

        int numPolls = 0;


        while (!queue.isEmpty()) {
            Node theNode = queue.poll();
            numPolls++;

            for (Node nextNode : wordEntries) {
                if (nextNode != startNode && nextNode.next == null && oneLetterDifferent(theNode.word, nextNode.word)) {
                    nextNode.next = theNode;
                    queue.offer(nextNode);
                    if (nextNode.word.equals(end)) {
                        String s = "";
                        for (Node node = nextNode; node != null; node = node.next) {
                            s = node.word + "\n" + s;
                        }
                        ui.sendMessage(s);
                        ui.sendMessage("This took " + numPolls + " polls.");
                        return;
                    }
                }
            }
        }
    }

    void solve3(String start, String end) {
        Queue<Node> queue = new Heap<>(new NodeComparator(end));

        Node startNode = find(start);
        queue.offer(startNode);

        int numPolls = 0;


        while (!queue.isEmpty()) {
            Node theNode = queue.poll();
            numPolls++;

            for (Node nextNode : wordEntries) {
                if (nextNode != startNode && nextNode.next == null && oneLetterDifferent(theNode.word, nextNode.word)) {
                    nextNode.next = theNode;
                    queue.offer(nextNode);
                    if (nextNode.word.equals(end)) {
                        String s = "";
                        for (Node node = nextNode; node != null; node = node.next) {
                            s = node.word + "\n" + s;
                        }
                        ui.sendMessage(s);
                        ui.sendMessage("This took " + numPolls + " polls.");
                        return;
                    }
                }
                else if (nextNode != startNode && oneLetterDifferent(theNode.word, nextNode.word) && (distanceToStart(nextNode) > (distanceToStart(theNode) + 1))) {
                    nextNode.next = theNode;
                    queue.remove(nextNode);
                    queue.offer(nextNode);
                }
            }
        }
    }
    public static boolean oneLetterDifferent(String start, String end) {
        int numDifferences = 0;

        if (end == null) {
            return false;
        }
        if (start.length() != end.length()) {
            return false;
        }
        for (int i = 0; i < end.length(); i++) {
                if (start.charAt(i) != end.charAt(i)) {
                    numDifferences++;
                    }
                }

        if (numDifferences == 1) {
            return true;
        }
        return false;
    }

    protected static class Node{
        String word;
        Node next;

        public Node(String line) {
            this.word = line;
        }
    }
    private Node find(String word) {
        for (Node entry: wordEntries) {
            if (word.equals(entry.word)) {
                return entry;
            }
        }
        return null;
    }

    static int lettersDifferent (String word, String target) {
        int differentLetters = 0;
        for (int letter = 0; letter < word.length(); letter++) {
            if (word.charAt(letter) != target.charAt(letter)) {
                differentLetters++;
            }
        }
        return differentLetters;
    }

    int distanceToStart (Node node) {
        int count = 0;
        for (Node n = node; n != null; n = n.next)
            count++;
        return count;
    }
    protected class NodeComparator implements Comparator<Node> {
        String target;

        NodeComparator(String target) {
            this.target = target;
        }
        int priority (Node node) {
            return distanceToStart(node) + lettersDifferent(node.word, target);
        }
        public int compare (Node nodeA, Node nodeB) {
            return priority(nodeA) - priority(nodeB);
        }

    }


    }
