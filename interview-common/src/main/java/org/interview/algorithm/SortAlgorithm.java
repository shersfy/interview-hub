package org.interview.algorithm;

public class SortAlgorithm {

    public static void main(String[] args) {

        int[] arr = {3, 6, 7, 8, 1, 1, 2, 4, 5, 6};

        for(int a :arr) {
            System.out.print(a+"\t");
        }
        
//        selectSortM(arr);
//        selectSortX(arr);
        bubbleSort(arr);
        System.out.println();
        
        for(int a :arr) {
            System.out.print(a+"\t");
        }

        
    }
    /**
     * 选择排序：每趟选择最小(大)的元素放在前面已排序的末尾，时间复杂度o(n2)
     * @param arr
     */
    public static void selectSortM(int[] arr){
        
        for(int i=0; i<arr.length; i++) {

            int min = i;

            for(int j=i+1; j<arr.length; j++) {
                if(arr[j] < arr[min]) {
                    min = j;
                }
            }

            if(min!=i) {
                int tmp  = arr[i];
                arr[i]   = arr[min];
                arr[min] = tmp;
            }

        }

    }
    /**
     * 选择排序，选择最大的 O(n2)
     * @param arr
     */
    public static void selectSortX(int[] arr){

        for(int i=0; i<arr.length; i++) {

            int max = 0;
            for(int j=0; j<arr.length-i; j++) {
                if(arr[j] > arr[max]) {
                    max = j;
                }
            }

            int tmp  = arr[arr.length-1-i];
            arr[arr.length-1-i] = arr[max];
            arr[max] = tmp;

        }

    }
    
    /**
     * 冒泡排序：两个相邻元素作比较，外层每一趟冒出一个最大(小)值，内层每次是否需要交互位置
     * @param arr
     */
    public static void bubbleSort(int[] arr) {
        
        for(int i=0; i<arr.length-1; i++) {
            
            for(int j=i+1; j<arr.length; j++) {
                
                if(arr[i] > arr[j]) {
                    int tmp = arr[i];
                    arr[i] = arr[j];
                    arr[j] = tmp;
                }
                
            }
            
        }
        
    }

}
