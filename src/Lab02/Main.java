package Lab02;


import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;


//need to adjust still for proper k values


public class Main {

    //these values are used to change the overall functioning of the program
    private static int maxN = 1000000;
    private static int maxK = 50;
    private static int maxD = 3;
    private static int maxNumber = 256;
    private static int minNumber = 0;
    private static int trials = 10;
    private static int StartN = 4;
    private static int StartK = 6;
    private static int StartD = 1;

    //Sample will hole the list of lists while copy is used to exchange rows in the list
    private static int[][] Sample = new int[maxN][maxK];
    private static int[][] ArrayForMergesort = new int[maxN][maxK];
    private static int[] CopyOfSample = new int[maxK];

    private static int maxPrefix = 17000000;
    private static int [][] RebuildRadixSort = new int[maxN][maxK];
    private static int []  RadixCount = new int[maxPrefix];

    //used to fill with random values
    private static Random random = new Random();



    public static void main(String[] args) {

        //length of list to be sorted
        long N;

        //the size of the strings to sort
        int k;

        int d;



        //for time and doubling ratios
        long timeStampBefore = 0;
        long timeStampAfter = 0;
        long[] AverageTime = new long[4];
        float[] TimeRatio = new float[4];
        long[] HoldTimes = new long[4];
        int AverageSlot = 0;

        //counter
        int i;

        // will be used to determine overall trial number
        int CurrentTrial = 0;

        //to get track of k time value placement whithin the array
        int k6 = 0;
        int k12 = 1;
        int k24 = 2;
        int k48 = 3;

        // used to switch which sort type will be used in current test
        int UseSort = 0;
        int SortTypes = 3;


        int isSorted;


        //This outer loop is responsible for changing the sort type
        while (UseSort < SortTypes) {
            //Set N to base value
            N = StartN;

            //Clean up and break apart the charts with handy labels
            switch (UseSort) {
                case 0:
                    System.out.println("Selection Sort:\n\n");
                    break;
                case 1:
                    System.out.println("Quick Sort:\n\n");
                    break;
                case 2:
                    System.out.println("Merge Sort:\n\n");
                    break;
            }


            //This loop will double the overall size of the list until a max value is reached
            while (N <= maxN) {
                //set k as base value
                k = StartK;


                //used to shorten time of selection sort if desired 131072
                if(UseSort == 0 && N == 131072)
                    break;

                //Used to switch between k's 6,12,24, and 48 values
                while (k <= maxK) {


                    //begin selection sort timed trials
                    while (CurrentTrial < trials) {

                        //generate a new list per trial
                        GenerateList(N, k);


                        //obtain a single timed trial, UseTheSort function determines which sort will be used
                        timeStampBefore = getCpuTime();
                        UseTheSort(UseSort, N, k);
                        timeStampAfter = getCpuTime();





                        //Add the time for each trial
                        AverageTime[AverageSlot] = AverageTime[AverageSlot] + (timeStampAfter - timeStampBefore);

                        // go to the next trial
                        CurrentTrial++;
                    }


                    //calculate the average using the overall number obtained on line 122 and the number of trials
                    AverageTime[AverageSlot] = AverageTime[AverageSlot] / trials;


                    //restore the list to all zeros
                    cleanLists(N);


                    //reset trials, begin next slot, and double k value (list depth)
                    CurrentTrial = 0;
                    AverageSlot++;
                    k = k * 2;

                }

                isSorted((int)N, k);

                //If first N in the list
                if (N == StartN) {
                    //Print a header for the chart

                    System.out.println(String.format("%20s%20s%20s%20s%20s%20s%20s%20s%20s",
                            "N",
                            "K Time Avg: 6",
                            "K Ratio 6",
                            "K Time Avg: 12",
                            "K Ratio 6",
                            "K Time Avg: 24",
                            "K Ratio 6",
                            "K Time Avg: 48",
                            "K Ratio 6"));

                    //print the first row of values using "n/a" for doubling ratios
                    System.out.println(String.format("%20s%20s%20s%20s%20s%20s%20s%20s%20s",
                            N,
                            AverageTime[k6],
                            "N/a",
                            AverageTime[k12],
                            "N/a",
                            AverageTime[k24],
                            "N/a",
                            AverageTime[k48],
                            "N/a"));


                    //Grab and hold the average values to use when finding the doubling ratio
                    for (k = 0; k < 4; k++)
                        HoldTimes[k] = AverageTime[k];
                }
                //on every other iteration after the first N:
                else {
                    //Calculate the doubling ratios
                    for (i = 0; i < 4; i++)
                        TimeRatio[i] = (float) (AverageTime[i] / HoldTimes[i]);

                    //print out the desired information on the rest of the chart of the first N

                    System.out.println(String.format("%20s%20s%20.2f%20s%20.2f%20s%20.02f%20s%20.02f",
                            N,
                            AverageTime[k6],
                            (float) AverageTime[k6] / HoldTimes[k6],
                            AverageTime[k12],
                            (float) AverageTime[k12] / HoldTimes[k12],
                            AverageTime[k24],
                            (float) AverageTime[k24] / HoldTimes[k24],
                            AverageTime[k48],
                            (float) AverageTime[k48] / HoldTimes[k48]));


                    //Grab and hold the average values to use when finding the doubling ratio
                }
                for (i = 0; i < 4; i++)
                    HoldTimes[i] = AverageTime[i];

                //double the list size
                N = N * 2;

                AverageSlot = 0;
            }
            UseSort++;
        }




        //Now switch and do the radix sort

        d = StartD;

        //This outer loop is responsible for changing the sort type
        while (d <= maxD) {
            //Set N to base value
            N = StartN;

            System.out.println("Radix Sort:\n\n");

            //Clean up and break apart the charts with handy labels
            switch (d) {
                case 1:
                    System.out.println("D = 1:\n\n");
                    break;
                case 2:
                    System.out.println("D = 2:\n\n");
                    break;
                case 3:
                    System.out.println("D = 3\n\n");
            }


            N = StartN;

            //This loop will double the overall size of the list until a max value is reached
            while (N <= maxN) {
                //set k as base value
                k = StartK;

                //Used to switch between k's 6,12,24, and 48 values
                while (k <= maxK) {


                    //begin selection sort timed trials
                    while (CurrentTrial < trials) {

                        //generate a new list per trial
                        GenerateList(N, k);


                        //obtain a single timed trial, UseTheSort function determines which sort will be used
                        timeStampBefore = getCpuTime();
                        radixSort((int) N, k, d);
                        timeStampAfter = getCpuTime();



                        //Add the time for each trial
                        AverageTime[AverageSlot] = AverageTime[AverageSlot] + (timeStampAfter - timeStampBefore);

                        // go to the next trial
                        CurrentTrial++;
                    }

                    //calculate the average using the overall number obtained on line 122 and the number of trials
                    AverageTime[AverageSlot] = AverageTime[AverageSlot] / trials;


                    //restore the list to all zeros
                    cleanLists(N);


                    //reset trials, begin next slot, and double k value (list depth)
                    CurrentTrial = 0;
                    AverageSlot++;
                    k = k * 2;

                }

                isSorted((int)N, k);

                //If first N in the list
                if (N == StartN) {
                    //Print a header for the chart

                    System.out.println(String.format("%20s%20s%20s%20s%20s%20s%20s%20s%20s",
                            "N",
                            "K Time Avg: 6",
                            "K Ratio 6",
                            "K Time Avg: 12",
                            "K Ratio 6",
                            "K Time Avg: 24",
                            "K Ratio 6",
                            "K Time Avg: 48",
                            "K Ratio 6"));

                    //print the first row of values using "n/a" for doubling ratios
                    System.out.println(String.format("%20s%20s%20s%20s%20s%20s%20s%20s%20s",
                            N,
                            AverageTime[k6],
                            "N/a",
                            AverageTime[k12],
                            "N/a",
                            AverageTime[k24],
                            "N/a",
                            AverageTime[k48],
                            "N/a"));


                    //Grab and hold the average values to use when finding the doubling ratio
                    for (k = 0; k < 4; k++)
                        HoldTimes[k] = AverageTime[k];
                }
                //on every other iteration after the first N:
                else {
                    //Calculate the doubling ratios
                    for (i = 0; i < 4; i++)
                        TimeRatio[i] = (float) (AverageTime[i] / HoldTimes[i]);

                    //print out the desired information on the rest of the chart of the first N

                    System.out.println(String.format("%20s%20s%20.2f%20s%20.2f%20s%20.02f%20s%20.02f",
                            N,
                            AverageTime[k6],
                            (float) AverageTime[k6] / HoldTimes[k6],
                            AverageTime[k12],
                            (float) AverageTime[k12] / HoldTimes[k12],
                            AverageTime[k24],
                            (float) AverageTime[k24] / HoldTimes[k24],
                            AverageTime[k48],
                            (float) AverageTime[k48] / HoldTimes[k48]));


                    //Grab and hold the average values to use when finding the doubling ratio
                }
                for (i = 0; i < 4; i++)
                    HoldTimes[i] = AverageTime[i];

                //double the list size
                N = N * 2;

                AverageSlot = 0;
            }
            d++;
        }





    }


    /*** Table of contents
     UseTheSort()
     GenerateLists()
     exchange()
     cleanLists()
     PrintLists()
     radixSort()
     SelectionSort()
     quicksort()
     Partition()
     mergesortBegin()
     mergesort()
     merge()
     mergeExchange()
     reverseMergeExchange()
     printListsForMerge()
     getCpuTime()
     */


    private static void isSorted(int N, int k)
    {
        int i = 0;
        int j = 0;


        System.out.println("Verifying the list of size " + N +  " is sorted.... ");

        while(i < N - 1)
        {


            while(Sample[i][j] == Sample[i+1][j] && j < maxK - 1)
                j++;


            if(Sample[i][j] <= Sample[i+1][j] ) {
                i++;
                j = 0;
            }
            else {
                System.out.println("Opps");
                return;
            }

        }

        System.out.println("Sorted!!");

        return;

    }

    private static void UseTheSort(int UseSort, long N, int k)
    {
        //Switch statement will determine which sort that is used
        switch(UseSort)
        {
            case 0:
                SelectionSort(N, k);
                break;
            case 1:
                quicksort(k, 0, (int)N - 1);
                break;           //     quicksort(k, 0, (int)N - 1);
            case 2:
                mergesortBegin((int)N, k);
                break;
        }
    }



    static void GenerateList(long listSize, long itemSize) {

        int i;
        int j;
        int offsetToPreventZero = 1;

        for (i = 0; i < listSize; i++)
        {
            for (j = 0; j < itemSize; j++)
            {
                Sample[i][j] = offsetToPreventZero + random.nextInt(maxNumber - minNumber);
            }
            //           System.out.println(Arrays.toString(Sample[i]));
            //           System.out.println("\n");
        }

        return;
    }



    private static void exchange(long itemSize, int i, int j)
    {
        int c;

        //Exchange two rows in the list
        for (c = 0; c < itemSize; c++)
        {
            CopyOfSample[c] = Sample[i][c];
            Sample[i][c] = Sample[j][c];
            Sample[j][c] = CopyOfSample[c];

        }

    }


    static void cleanLists(long listSize)
    {
        int i;
        int j;
        char reset = 0;

        //clean the main array and set all values to 0
        for (i = 0; i < listSize; i++)
        {
            for (j = 0; j < maxK; j++)
            {
                Sample[i][j] = reset;
            }
        }

        return;
    }


    //if needed is used to print out the main array
    static void PrintLists(long listSize) {
        int i;
        int j;
        char reset = 0;


        for (i = 0; i < listSize; i++) {

            System.out.println(i + ": " + Arrays.toString(Sample[i]));

        }

        return;
    }


    static void radixSort(int listSize, int itemSize, int byteSize)
    {


        int StartAtEndStringIndex = itemSize - 1;


        int countSlot;
        int countSlot2;
        int i;
        int grabValue;
        int grabValue2;
        int grabValue3;
        int grabIndex;


        while(StartAtEndStringIndex >= 0) {

            //get a count
            switch(byteSize) {
                //get a count of the last character
                case 1:
                    for (int j = 0; j < listSize; j++) {
                        countSlot = Sample[j][StartAtEndStringIndex];
                        RadixCount[countSlot]++;
                    }
                    break;
                //get a count of the last 2 numbers
                case 2:
                    for (int j = 0; j < listSize; j++) {
                        countSlot = Sample[j][StartAtEndStringIndex - 1];
                        countSlot = countSlot * maxNumber + Sample[j][StartAtEndStringIndex];
                        RadixCount[countSlot]++;
                    }
                    break;
                //get a count of the last 3 numbers
                case 3:
                    for (int j = 0; j < listSize; j++) {
                        countSlot = Sample[j][StartAtEndStringIndex - 2];
                        countSlot2 = Sample[j][StartAtEndStringIndex - 1];
                        countSlot = countSlot * maxNumber * maxNumber + countSlot2 * maxNumber + Sample[j][StartAtEndStringIndex];
                        RadixCount[countSlot]++;
                    }
                    break;
            }



            //Change the count list into a prefix list, should work for all d
            for (int j = 1; j < maxPrefix; j++) {
                i = j - 1;
                RadixCount[j] = RadixCount[i] + RadixCount[j];
            }





            switch(byteSize) {
                case 1:
                    //if d = 1 get the index
                    for (int j = listSize - 1; j >= 0; j--) {
                        grabValue = Sample[j][StartAtEndStringIndex];
                        RadixCount[grabValue]--;
                        grabIndex = RadixCount[grabValue];

                        //move the string into the proper index on the Extra Array
                        for (int k = 0; k < itemSize; k++) {
                            RebuildRadixSort[grabIndex][k] = Sample[j][k];
                        }
                    }
                    break;
                case 2:
                    //if d = 2, get the index
                    for (int j = listSize - 1; j >= 0; j--) {
                        grabValue = Sample[j][StartAtEndStringIndex - 1];
                        grabValue2 = Sample[j][StartAtEndStringIndex];
                        grabValue = grabValue * maxNumber + grabValue2;

                        RadixCount[grabValue]--;
                        grabIndex = RadixCount[grabValue];

                        //move the string into the proper index on the Extra Array
                        for (int k = 0; k < itemSize; k++) {
                            RebuildRadixSort[grabIndex][k] = Sample[j][k];
                        }
                    }
                    break;
                case 3:
                    //if d = 3 get the index
                    for (int j = listSize - 1; j >= 0; j--) {
                        grabValue = Sample[j][StartAtEndStringIndex - 2];
                        grabValue2 = Sample[j][StartAtEndStringIndex - 1];
                        grabValue3 = Sample[j][StartAtEndStringIndex];
                        grabValue = grabValue * maxNumber * maxNumber + grabValue2 * maxNumber + grabValue3;

                        RadixCount[grabValue]--;
                        grabIndex = RadixCount[grabValue];

                        //move the string into the proper index on the Extra Array
                        for (int k = 0; k < itemSize; k++) {
                            RebuildRadixSort[grabIndex][k] = Sample[j][k];
                        }
                    }

            }



            //reset the RadixCount array
            for(int j = 0; j < maxPrefix; j++)
                RadixCount[j] = 0;



            //depending on the d value, decrement the counter
            switch(byteSize) {
                case 1:
                    StartAtEndStringIndex--;
                    break;
                case 2:
                    StartAtEndStringIndex = StartAtEndStringIndex - 2;
                    break;
                case 3:
                    StartAtEndStringIndex = StartAtEndStringIndex - 3;
            }



            //if the end of the list is reached, copy the temp array into the wanted array
            if(StartAtEndStringIndex <= 0) {
                for(int j = 0; j < listSize; j++)
                    for(int k = 0; k < itemSize; k++)
                        Sample[j][k] = RebuildRadixSort[j][k];
                break;
            }



            /** Begin next Pass in reverse order to put back into origional array                                   **/
            //get a count of the last character
            switch(byteSize) {
                //get a count of the last character
                case 1:
                    for (int j = 0; j < listSize; j++) {
                        countSlot = RebuildRadixSort[j][StartAtEndStringIndex];
                        RadixCount[countSlot]++;
                    }
                    break;
                //get a count of the last 2 numbers
                case 2:
                    for (int j = 0; j < listSize; j++) {
                        countSlot = RebuildRadixSort[j][StartAtEndStringIndex - 1];
                        countSlot = countSlot * maxNumber + RebuildRadixSort[j][StartAtEndStringIndex];
                        RadixCount[countSlot]++;/********/
                    }
                    break;
                //get a count of the last 3 numbers
                case 3:
                    for (int j = 0; j < listSize; j++) {
                        countSlot = RebuildRadixSort[j][StartAtEndStringIndex - 2];
                        countSlot2 = RebuildRadixSort[j][StartAtEndStringIndex - 1];
                        countSlot = countSlot * maxNumber * maxNumber + countSlot2 * maxNumber + RebuildRadixSort[j][StartAtEndStringIndex];
                        RadixCount[countSlot]++;
                    }
                    break;
            }

            //prefix calculater
            for (int j = 1; j < maxPrefix; j++) {
                i = j - 1;
                RadixCount[j] = RadixCount[i] + RadixCount[j];
            }



            switch(byteSize) {
                case 1:
                    // if d = 1 get the index
                    for (int j = listSize - 1; j >= 0; j--) {
                        grabValue = RebuildRadixSort[j][StartAtEndStringIndex];
                        RadixCount[grabValue]--;
                        grabIndex = RadixCount[grabValue];

                        //move the string into the proper index on the Extra Array
                        for (int k = 0; k < itemSize; k++) {
                            Sample[grabIndex][k] = RebuildRadixSort[j][k];
                        }
                    }
                    break;
                case 2:
                    // if d = 2, get the index
                    for (int j = listSize - 1; j >= 0; j--) {
                        grabValue = RebuildRadixSort[j][StartAtEndStringIndex - 1];
                        grabValue2 = RebuildRadixSort[j][StartAtEndStringIndex];
                        grabValue = grabValue * maxNumber + grabValue2;

                        RadixCount[grabValue]--;
                        grabIndex = RadixCount[grabValue];

                        //move the string into the proper index on the Extra Array
                        for (int k = 0; k < itemSize; k++) {
                            Sample[grabIndex][k] = RebuildRadixSort[j][k];
                        }
                    }
                    break;
                case 3:
                    //if d = 3 get the index
                    for (int j = listSize - 1; j >= 0; j--) {
                        grabValue = RebuildRadixSort[j][StartAtEndStringIndex - 2];
                        grabValue2 = RebuildRadixSort[j][StartAtEndStringIndex - 1];
                        grabValue3 = RebuildRadixSort[j][StartAtEndStringIndex];
                        grabValue = grabValue * maxNumber * maxNumber + grabValue2 * maxNumber + grabValue3;

                        RadixCount[grabValue]--;
                        grabIndex = RadixCount[grabValue];


                        //move the string into the proper index on the Extra Array
                        for (int k = 0; k < itemSize; k++) {
                            Sample[grabIndex][k] = RebuildRadixSort[j][k];
                        }
                        break;
                    }
            }

            //reset count
            for(int j = 0; j < maxPrefix; j++)
                RadixCount[j] = 0;


            //decrement the list values depending on the value of d
            switch(byteSize) {
                case 1:
                    StartAtEndStringIndex--;
                    break;
                case 2:
                    StartAtEndStringIndex = StartAtEndStringIndex - 2;
                    break;
                case 3:
                    StartAtEndStringIndex = StartAtEndStringIndex - 3;
            }

            //GO ahead and break if no more passes can be done
            if(StartAtEndStringIndex <= 0)
                break;

        }

    }


    static void SelectionSort(long listSize, long itemSize)
    {
        int i;
        int j;

        int k = 0;

        int lowestValue;
        int lowestPlace;



        while(k < listSize)
        {
            //k will determine how many of the elements are already sorted
            //lowest place and value will determine the lowest value detected as well as the row it sits in.
            lowestValue = Sample[k][0];
            lowestPlace = k;


            //obtain the lowest row
            for (i = k + 1; i < listSize; i++)
            {
                //looks at the first column of each row to see if it is smaller then the current stored value
                if (Sample[i][0] < lowestValue)
                {
                    lowestValue = Sample[i][0];
                    lowestPlace = i;
                }

                //If a value is the same as the current lowest, traverse down both lists until one emerges with the smallest number
                if (Sample[i][0] == lowestValue)
                {
                    j = 0;

                    //traverse down the list until the numbers are not equivelent
                    while (Sample[i][j] == Sample[lowestPlace][j] && j < maxK - 1)
                        j++;

                    //when arriving at the lowest digit, set it as the overall lowest
                    if (Sample[i][j] < Sample[lowestPlace][j])
                    {
                        lowestValue = Sample[i][0];
                        lowestPlace = i;
                    }
                }
            }


            exchange(itemSize, k, lowestPlace);

            //increment k so say that the current row is in its final position
            k++;
        }
        return;
    }



    static void quicksort(long itemSize, int low, int high) {

        //return right away if list is either empty or has only one item
        if (high <= low)
            return;


        int index = Partition(itemSize, low, high);

        //sort the left side of the list
        quicksort(itemSize, low, index - 1);

        //sort the right side of the list
        quicksort(itemSize, index + 1, high);

    }



    private static int Partition(long itemSize, int low, int high)
    {
        int i = low;
        int j = high;

        int listItemForI;
        int listItemForJ;
        int tempPlaceHolder;

        //pick the middle element of the list, and make it into a pivot, save location as pivotPoint
        int pivotPoint = (low + high) / 2;
        int pivot = Sample[pivotPoint][0];


        while(true) {

            listItemForI = 0;



            //Traverse down the list from the left until reaching something greater or equal to the pivot
            while (Sample[i][listItemForI] <= Sample[pivotPoint][listItemForI]) {
                if (i == high)
                    break;

                while(Sample[i][listItemForI] == Sample[pivotPoint][listItemForI] && listItemForI < itemSize - 1)
                    listItemForI++;

                if(Sample[i][listItemForI] <= Sample[pivotPoint][listItemForI] && i + 1 != j) {
                    i++;
                    listItemForI = 0;
                }

                if(i+1 == j)
                    break;
            }



            listItemForJ = 0;

            //Traverse down the list from the left until reaching something greater or equal to the pivot
            while (Sample[pivotPoint][listItemForJ] <= Sample[j][listItemForJ]) {
                if (j == low)
                    break;

                while(Sample[j][listItemForJ] == Sample[pivotPoint][listItemForJ] && listItemForJ < itemSize - 1)
                    listItemForJ++;

                if(Sample[pivotPoint][listItemForJ] <= Sample[j][listItemForJ] && j - 1 != i) {
                    j--;
                    listItemForJ = 0;
                }

                if(j - 1 == i )
                    break;

            }

            //         System.out.println("\n Pivot " + pivotPoint + ": " + pivotPoint);
            //        System.out.println("Grab i and j: " + i + " + " + j + "\n");


            if(i >= j)
                break;

            if(i + 1 == j)
            {
                listItemForI = 0;
                listItemForJ = 0;
                tempPlaceHolder = 0;

                while(Sample[i][listItemForI] == Sample[j][listItemForI] && listItemForI < itemSize - 1)
                    listItemForI++;


                if(Sample[i][listItemForI] > Sample[j][listItemForI])
                    exchange(itemSize, i, j);


                tempPlaceHolder = i;
                listItemForI = 0;



                //go through the list till the numbers being compared are not equal
                while(Sample[i][listItemForI] == Sample[pivotPoint][listItemForI] && listItemForI < itemSize - 1)
                    listItemForI++;

                if(Sample[i][listItemForI] > Sample[pivotPoint][listItemForI] && i < pivotPoint)
                    exchange(itemSize, i, pivotPoint);

                if(Sample[i][listItemForI] < Sample[pivotPoint][listItemForI] && i > pivotPoint)
                    exchange(itemSize, i, pivotPoint);


                //             System.out.println("Final:" + i + "\n");
                //             PrintLists(8);
                break;
            }


            exchange(itemSize, i, j);
            //         PrintLists(8);

        }


        //       exchange(itemSize, low, pivotPoint);

        return i;
    }


    public static void mergesortBegin(int listSize, int itemSize)
    {
        //being the call here
        mergesort(0, listSize - 1, listSize, itemSize);
    }

    //this one is the recursive call
    public static void mergesort(int low, int high, int listSize, int itemSize) {
        //will return if theres only a single element
        if(low >= high)
            return;

        //get the middle element
        int mid = (low + high) / 2;

        //split off the lower half
        mergesort(low, mid, listSize, itemSize);

        //split off hte top half
        mergesort(mid + 1, high, listSize, itemSize);

        //merge the two arrays
        merge(low, high, listSize, itemSize);
    }



    public static void merge(int low, int high, int listSize, int itemSize)
    {
        //get end of first list and beginning of second list
        int endOfLowList = (low + high) / 2;
        int beginSecondList = endOfLowList + 1;

        //get the start of both lists
        int leftArrayStart = low;
        int rightArrayStart = beginSecondList;

        //current index determines which value on the tmp array
        int currentIndex = low;

        //compareSlot is used to walk down the list of k values
        int compareSlot;


        //while(leftArrayStart <= endOfLowList && rightArrayStart <= high)
        while(currentIndex <= high)
        {
            compareSlot = 0;

            //walk down the list until the values are not the same
            while(Sample[leftArrayStart][compareSlot] == Sample[rightArrayStart][compareSlot] && compareSlot < itemSize - 1)
                compareSlot++;


            //if left list value is smaller than right list value & left list value isnt empty
            if (Sample[leftArrayStart][compareSlot] <= Sample[rightArrayStart][compareSlot] && Sample[leftArrayStart][compareSlot] != 0)
            {
                //push the left array value to the temp array
                mergeExchange(itemSize, leftArrayStart, currentIndex);

                //if left list isnt at the end, go to the next element
                if(leftArrayStart != endOfLowList)
                    leftArrayStart++;
            }
            //if right list value is smaller than left list value & right list value isnt empty
            else if (Sample[rightArrayStart][compareSlot] < Sample[leftArrayStart][compareSlot] && Sample[rightArrayStart][compareSlot] != 0)
            {
                //push the right array value to temp array
                mergeExchange(itemSize, rightArrayStart, currentIndex);

                //if right list isnt at the end of the list increment to the next value
                if(rightArrayStart != high)
                    rightArrayStart++;
            }
            //if left array value is 0 and right array still has elements
            else if (Sample[leftArrayStart][compareSlot] == 0 && Sample[rightArrayStart][compareSlot] != 0)
            {
                //push the right array to the temp array
                mergeExchange(itemSize, rightArrayStart, currentIndex);

                //go to the next element in the right list if it isnt the final element
                if(rightArrayStart != high)
                    rightArrayStart++;
            }
            //if right list is empty and left list still has values
            else if(Sample[rightArrayStart][compareSlot] == 0 && Sample[leftArrayStart][compareSlot] != 0)
            {
                //push left array onto the temp array
                mergeExchange(itemSize, leftArrayStart, currentIndex);

                //if left list isnt empty, go to the next element
                if (leftArrayStart != endOfLowList)
                    leftArrayStart++;
            }

            //increment the temp array
            currentIndex++;
        }

        //push all the values from the temp array back into the main array so recursion can grab values
        for(int i = low; i <= high; i++)
        {
            reverseMergeExchange(itemSize, i, i );
        }
    }


    private static void mergeExchange(long itemSize, int mainArrayIndex, int tmpArrayIndex)
    {
        int c;

        //push the values from the main array to the temp array then change all the values in the main array to 0 to signal it has been used
        for (c = 0; c < itemSize; c++)
        {
            ArrayForMergesort[tmpArrayIndex][c] = Sample[mainArrayIndex][c];
            Sample[mainArrayIndex][c] =  CopyOfSample[c];
        }
    }


    private static void reverseMergeExchange(long itemSize, int mainArrayIndex, int tmpArrayIndex)
    {
        int c;

        //Push all the values back into the main array and empty the temp array
        for (c = 0; c < itemSize; c++)
        {
            Sample[mainArrayIndex][c] = ArrayForMergesort[tmpArrayIndex][c];
            ArrayForMergesort[tmpArrayIndex][c] =  CopyOfSample[c];
        }
    }






    //if needed, was used to help picture the merge sort
    static void printListsForMerge(int low, long listSize)
    {

        if(listSize != maxN) {
            while (low <= listSize) {

                System.out.println(low + ": " + Arrays.toString(ArrayForMergesort[low]));
                low++;
            }
        }
        else {
            while (low < listSize) {

                System.out.println(low + ": " + Arrays.toString(ArrayForMergesort[low]));
                low++;

            }
        }

        System.out.println("\n");

        return;
    }


/** Get CPU time in nanoseconds since the program(thread) started. */
    /** from: http://nadeausoftware.com/articles/2008/03/java_tip_how_get_cpu_and_user_time_benchmarking#TimingasinglethreadedtaskusingCPUsystemandusertime **/
    public static long getCpuTime( )
    {
        ThreadMXBean bean = ManagementFactory.getThreadMXBean( );
        return bean.isCurrentThreadCpuTimeSupported( ) ?
                bean.getCurrentThreadCpuTime( ) : 0L;
    }

}



