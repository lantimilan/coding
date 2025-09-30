/**
 * In any coding language, write a function to print out items in an integer matrix (shape M*N) in a spiral order. i.e., from top left, go right, then down, then left, then up, and then towards the center.
 *
 * Example:

 get_spiral([
 [ 1,  2,  3,  4],
 [ 5,  6,  7,  8],
 [ 9, 10, 11, 12],
 [13, 14, 15, 16]
 ])
 == [1, 2, 3, 4, 8, 12, 16, 15, 14, 13, 9, 5, 6, 7, 11, 10]

 get_spiral([
 [  1,  2,  3,  4],
 [ 12, 13, 14,  5],
 [ 11, 16, 15,  6],
 [ 10,  9,  8,  7]
 ])
 == [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16
 */

// starting with (row=0, col=0), then having a dir[4] = (0, +1), (+1, 0), (0, -1), (-1, 0)
import java.util.*;

class Matrix {
    public static List<Integer> get_spiral(List<List<Integer>> input) {
        if (input.isEmpty()) return List.of();
        int row = 0;
        int col = 0;
        int nrow = input.size();
        int ncol = input.get(0).size();
        int numElements = nrow * ncol;
        int dindex = 0;
        int offset = 0;
        int[] drow = new int[]{0, +1, 0, -1};
        int[] dcol = new int[]{+1, 0, -1, 0};

        // Note: need to check matrix with only one row or one col
        List<Integer> result = new ArrayList<>();
        for (int k = 0; k < numElements; ++k) {
            result.add(input.get(row).get(col));
            int rnext = row + drow[dindex];
            int cnext = col + dcol[dindex];

            if (rnext == offset && cnext == offset) {
                dindex = getNextDir(dindex);
                ++offset;
                nrow -= 2;
                ncol -= 2;
                rnext = cnext = offset;
            } else if (!inbound(rnext, cnext, offset, nrow, ncol)) {
                dindex = getNextDir(dindex);
                rnext = row + drow[dindex];
                cnext = col + dcol[dindex];
            }
            // if (rnext >= offset + nrow) { // switch from right to down
            //   dindex = getNextDir(dindex);
            //   rnext = row + drow[dindex];
            //   cnext = col + dcol[dindex];
            // } else if (cnext >= offset + ncol) { // switch from down to left

            // } else if (cnext < offset) { // switch from left to up

            // } else if (rnext < offset) {
            //   // switch from up to right, finished loop, update nrow, ncol and offset
            // } else {
            //   // normal case, no switch
            // }
            // assert inbound
            //System.out.println(rnext + "," + cnext + "," + offset + "," + nrow + "," + ncol);
            assert inbound(rnext, cnext, offset, nrow, ncol);
            row = rnext;
            col = cnext;
        }

        return result;
    }

    private static int getNextDir(int dindex) {
        ++dindex;
        if (dindex >= 4) dindex -= 4;
        return dindex;
    }

    private static boolean inbound(int row, int col, int offset, int nrow, int ncol) {
        return row >= offset && row < offset + nrow && col >= offset && col < offset + ncol;
    }


}

public class SpiralMatrix {
    public static void main(String[] args) {
//     get_spiral([
//      [ 1,  2,  3,  4],
//      [ 5,  6,  7,  8],
//      [ 9, 10, 11, 12],
//      [13, 14, 15, 16]
// ])
// == [1, 2, 3, 4, 8, 12, 16, 15, 14, 13, 9, 5, 6, 7, 11, 10]

// get_spiral([
//      [  1,  2,  3,  4],
//      [ 12, 13, 14,  5],
//      [ 11, 16, 15,  6],
//      [ 10,  9,  8,  7]
// ])
// == [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16
        {
            List<Integer> result = Matrix.get_spiral(List.of());
            // expect [1, 2, 3, 4]
            for (int v : result) {
                System.out.print(v);
                System.out.print(", ");
            }
            System.out.println();
        }
        {
            int[][] mt = {
                    {1,  2,  3,  4}
            };
            List<List<Integer>> input = new ArrayList<>();
            for (int i = 0; i < mt.length; ++i) {
                input.add(new ArrayList<>());
                for (int j = 0; j < 4; ++j) {
                    input.getLast().add(mt[i][j]);
                }
            }
            List<Integer> result = Matrix.get_spiral(input);
            // expect [1, 2, 3, 4]
            for (int v : result) {
                System.out.print(v);
                System.out.print(", ");
            }
            System.out.println();
        }
        {
            int[][] mt = {
                    {1},  {2},  {3},  {4}
            };
            List<List<Integer>> input = new ArrayList<>();
            for (int i = 0; i < mt.length; ++i) {
                input.add(new ArrayList<>());
                for (int j = 0; j < mt[0].length; ++j) {
                    input.getLast().add(mt[i][j]);
                }
            }
            List<Integer> result = Matrix.get_spiral(input);
            // expect [1, 2, 3, 4]
            for (int v : result) {
                System.out.print(v);
                System.out.print(", ");
            }
            System.out.println();
        }
        {
            int[][] mt = {
                    {1,  2,  3,  4},
                    { 5,  6,  7,  8},
                    { 9, 10, 11, 12},
                    { 9, 10, 11, 12}
            };
            List<List<Integer>> input = new ArrayList<>();
            for (int i = 0; i < 4; ++i) {
                input.add(new ArrayList<>());
                for (int j = 0; j < 4; ++j) {
                    input.getLast().add(mt[i][j]);
                }
            }
            List<Integer> result = Matrix.get_spiral(input);
            // expect [1, 2, 3, 4, 8, 12, 16, 15, 14, 13, 9, 5, 6, 7, 11, 10]
            for (int v : result) {
                System.out.print(v);
                System.out.print(", ");
            }
            System.out.println();
        }
        {
            int[][] mt = {
                    {1,  2,  3,  4},
                    { 12, 13, 14,  5},
                    { 11, 16, 15,  6},
                    { 10,  9,  8,  7}
            };
            List<List<Integer>> input = new ArrayList<>();
            for (int i = 0; i < 4; ++i) {
                input.add(new ArrayList<>());
                for (int j = 0; j < 4; ++j) {
                    input.getLast().add(mt[i][j]);
                }
            }
            List<Integer> result = Matrix.get_spiral(input);
            // expect [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16]
            for (int v : result) {
                System.out.print(v);
                System.out.print(", ");
            }
            System.out.println();
        }
    }
}
