package rta;

import words.TimeWord;

public class TimeGuard implements Cloneable{

    public static final int MAX_TIME = 1000;

    private boolean leftOpen;
    private boolean rightOpen;
    private int left;
    private int right;

    public TimeGuard(){}

    public TimeGuard(String pattern){
        pattern = pattern.trim();
        if(pattern.charAt(0) == '['){
            setLeftOpen(false);
        }else if(pattern.charAt(0) == '('){
            setLeftOpen(true);
        }else {
            throw new RuntimeException("guard pattern error");
        }
        int size = pattern.length();
        if(pattern.charAt(size-1) == ']'){
            setRightOpen(false);
        }else if(pattern.charAt(size-1) == ')'){
            setRightOpen(true);
        }else {
            throw new RuntimeException("guard pattern error");
        }
        String[] numbers = pattern.split("\\,|\\[|\\(|\\]|\\)");
        int left = Integer.parseInt(numbers[1]);
        int right;
        if(numbers[2].equals("+")){
            right = MAX_TIME;
        }else {
            right = Integer.parseInt(numbers[2]);
        }
        setLeft(left);
        setRight(right);
    }

    public TimeGuard(boolean leftOpen, boolean rightOpen, int left, int right) {
        this.leftOpen = leftOpen;
        this.rightOpen = rightOpen;
        this.left = left;
        this.right = right;
    }



    public TimeGuard(TimeWord timeWord){
        double time = timeWord.getValue();
        if(time == (int)time){
            this.leftOpen = false;
            this.rightOpen = false;
            this.left = (int)time;
            this.right = (int)time;
        }
        else {
            this.leftOpen = true;
            this.rightOpen = true;
            this.left = (int)time;
            this.right = (int)time+1;
        }
    }

    public boolean isLeftOpen() {
        return leftOpen;
    }

    public void setLeftOpen(boolean leftOpen) {
        this.leftOpen = leftOpen;
    }

    public boolean isRightOpen() {
        return rightOpen;
    }

    public void setRightOpen(boolean rightOpen) {
        this.rightOpen = rightOpen;
    }

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getRight() {
        return right;
    }

    public void setRight(int right) {
        this.right = right;
    }


    public boolean isPass(double value){

        if(leftOpen && rightOpen){
            if(value > left && value < right){
                return true;
            }
            return false;
        }
        if(!leftOpen && rightOpen){
            if(value >= left && value < right){
                return true;
            }
            return false;
        }
        if(leftOpen && !rightOpen){
            if(value > left && value <= right){
                return true;
            }
            return false;
        }
        if(!leftOpen && !rightOpen){
            if(value >= left && value <= right){
                return true;
            }
            return false;
        }
        return false;
    }

    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        if(leftOpen){
            stringBuilder.append("(");
        }
        else {
            stringBuilder.append("[");
        }
        stringBuilder.append(left).append(",").append(right);
        if(rightOpen){
            stringBuilder.append(")");
        }else {
            stringBuilder.append("]");
        }
        return stringBuilder.toString();
    }

    @Override
    public TimeGuard clone() throws CloneNotSupportedException {
        return (TimeGuard) super.clone();
    }

    public String toExpression(){
        StringBuilder stringBuilder = new StringBuilder();
        if(leftOpen){
            stringBuilder.append("x>"+left);
        }else {
            stringBuilder.append("x >="+left);
        }
        stringBuilder.append(" && ");
        if(rightOpen){
            stringBuilder.append("x<"+right);
        }else {
            stringBuilder.append("x<="+right);
        }
        return stringBuilder.toString();
    }

    //求和另一个timeGuard的交集
    public TimeGuard intersection(TimeGuard timeGuard){
        int left,right;
        boolean leftOpen, rightOpen;
        //左边小
        boolean leftLess = (this.left < timeGuard.left) ||
                (this.left == timeGuard.left && timeGuard.leftOpen);
        //右边大
        boolean rightMore = (this.right > timeGuard.right) ||
                (this.right == timeGuard.right && !timeGuard.rightOpen);

        if(leftLess){
            left = timeGuard.left;
            leftOpen = timeGuard.leftOpen;
        }else{
            left = this.left;
            leftOpen = this.leftOpen;
        }

        if(rightMore){
            right = timeGuard.right;
            rightOpen = timeGuard.rightOpen;
        }else{
            right = this.right;
            rightOpen = this.rightOpen;
        }

        boolean ok = (left < right) || (left == right && !leftOpen && !rightOpen);

        if(ok){
            return new TimeGuard(leftOpen,rightOpen,left,right);
        }
        return null;
    }

    @Override
    public int hashCode(){
        return left+right*2;
    }

    @Override
    public boolean equals(Object o){
        TimeGuard timeGuard = (TimeGuard)o;
        if(this.leftOpen==timeGuard.leftOpen && this.left == timeGuard.left &&
            this.right == timeGuard.right && this.rightOpen == timeGuard.rightOpen){
            return true;
        }
        return false;
    }
}
