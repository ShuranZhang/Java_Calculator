import java.util.*;
public class URCalculator {
	public static Map<String, Integer>operators = new HashMap<String, Integer>();//restore operators and precedence
	public static Map<String, Double> variable = new HashMap<String, Double>();//restore variables and their values
	static void setOperations() {//set the opertors with corresponding precedence
		operators.put("+", 1);  
		operators.put("-", 1);  
		operators.put("*", 2);  
		operators.put("/", 2);
		operators.put("!", 2);
		operators.put("#", 3);//temporary single length sign to represent sin
		operators.put("~", 3);//temporary single length sign to represent cos
		operators.put("&", 3);//temporary single length sign to represent tan
		operators.put("β", 3); //temporary single length sign to represent log(natural log)
		operators.put("^", 4);
		operators.put("{", 5);  
		operators.put("}", 5);  
		operators.put("]", 5);
		operators.put("[", 5);
		operators.put(")", 5);
		operators.put("(", 5);
	}
	private static boolean checkAssociative(String string) {//use to make sure operators are right associative
		return false;
	}
	public static boolean isOp(String string) {//check if the token is an operator
		try {
			int key=operators.get(string);
			return true;
		} catch(NullPointerException e) {
			return false;
		}	
	}
	public static Queue<String> converter(String str) {//method to convert the user input to postifx as a queue
		Queue<String> misMatch= new LinkedList<String>();
		misMatch.add("Mismatch Parentheses");//return this when a mismatch parenthesis happened
		try {
			Stack<String> ops = new Stack<String>();//stack to store operators
			Queue<String> input = new LinkedList<String>();//Queue to store the original expression
			Queue<String> result = new LinkedList<String>();//Queue to store the post suffix expression
			char[] originalInput = str.toCharArray();
			ArrayList<Character> PlusMinusLogic = new ArrayList<Character>();//List use to eliminate consecutive +/-
			for(char s: originalInput) {
				if(isOp(s+"")&&operators.get(s+"")==1) {//if the token is +/-
					if(PlusMinusLogic.size()==0) {//situation when +/- are in front of the whole input
						if(s=='-') {
							PlusMinusLogic.add(0, '0');
							PlusMinusLogic.add(s);
						}
					}else if((s=='-')&&PlusMinusLogic.get(PlusMinusLogic.size()-1)=='('||PlusMinusLogic.get(PlusMinusLogic.size()-1)=='['||PlusMinusLogic.get(PlusMinusLogic.size()-1)=='{'){
						//situation when +/- are in front of the open parentheses
						PlusMinusLogic.add('0');
						PlusMinusLogic.add(s);
					}else if(PlusMinusLogic.get(PlusMinusLogic.size()-1)=='+'||PlusMinusLogic.get(PlusMinusLogic.size()-1)=='-') {
						if(s==PlusMinusLogic.get(PlusMinusLogic.size()-1)) {
							PlusMinusLogic.set(PlusMinusLogic.size()-1, '+');//change the sign to + if two consecutive +/- are same
						}else {
							PlusMinusLogic.set(PlusMinusLogic.size()-1, '-');//otherwise change to -
						}
					}else {
						PlusMinusLogic.add(s);
					}
				}else {
					PlusMinusLogic.add(s);
				}
			}
			char[] update=new char[PlusMinusLogic.size()+1];//create a new array to separate each number and operator
			for(int i=0;i<PlusMinusLogic.size();i++) {
				update[i]=PlusMinusLogic.get(i);
			}
			update[PlusMinusLogic.size()]='@';//set the last char to @ to handle the situation that the last token is number
			String st="";
			for(char s: update) {
				if(isOp(s+"")==false&&s!='@') {
					st+=s;//keep updating the string is it's a multiple digits number/decimal
				}else if(s=='@'){
					input.add(st);
				}else{
					input.add(st);//add the updated number
					st="";//reset the string that stores the multiple digits/decimals
					if(s!='@') 
						input.add(s+"");	
				}
			}
			for(int i=0;i<input.size();i++) {//remove all ""that used to convert char into string
				if(input.contains("")) {
					input.remove("");
				}
			}
			int size=input.size();
			for (int i = 0; i <size; i++) {//main shunting yard algorithm loop
				//dealing with stuff inside the parentheses: keeping popping from open parentheses
				//until reach the close parentheses
				if (input.peek().equals("(")) {
					ops.push(input.poll());
				}else if (input.peek().equals(")")) {
					while (!ops.peek().equals("(")) {
						result.add(ops.pop());
					}
					input.poll();
					ops.pop(); 
				}else if (input.peek().equals("[")) {
					ops.push(input.poll());
				}else if (input.peek().equals("]")) {
					while (!ops.peek().equals("[")) {
						result.add(ops.pop());
					}
					input.poll();
					ops.pop(); 
				}else if (input.peek().equals("{")) {
					ops.push(input.poll());
				}else if (input.peek().equals("}")) {
					while (!ops.peek().equals("{")) {
						result.add(ops.pop());
					}
					input.poll();
					ops.pop(); 
				}
				else if (!isOp(input.peek())) {//add all the number to the result stack directly
					result.add(input.poll());
				}else{//dealing with polling operators from the operators queue when necessary 
					boolean popped = false;
					while (!popped) {//polling the associative operators inside one parentheses
						if (ops.isEmpty() || ops.peek().equals("(")||ops.peek().equals("[")||ops.peek().equals("{")) {  
							ops.push(input.poll());
							popped = true;
						}else{//normal situation when polling operators based on precedence
							String topOp = ops.peek();
							if ((operators.get(input.peek()) >operators.get(topOp))||operators.get(input.peek())==operators.get(topOp)&&checkAssociative(input.peek())) {
								ops.push(input.poll());
								popped = true;
							}else {
								result.add(ops.pop());
							} 
						} 
					} 
				} 
			} 
			while (! ops.isEmpty()) {
				result.add(ops.pop());
			}

			for(String s: result) {
				if(s.equals("(")||s.equals("[")||s.equals("{")) {
					return misMatch;
				}
			}

			return result;
		}catch(EmptyStackException e) {//an empty stack exception happens when the expression is not well formed
			return misMatch;//inform the user
		}
	}
	public static boolean isNum(String st) {//use to detect if the input string is a number
		String num="0123456789.";
		for(int i=0;i<st.length();i++) {
			if(!num.contains(st.charAt(i)+"")) 
				return false;
		}
		return true;
	}
	private static String evoe(Queue<String> st){//method use to calculate the result
		try {
			boolean divbyZero=false;//use to keep an eye on whether the expression is divide by zero
			Stack<String> stack = new Stack<String>();//stack use to evaluating and store the final answer
			int size=st.size();
			for(int i=0;i<size;i++){
				String s=st.poll();
				if(isOp(s)==false){
					stack.push(s);//if it's number push into the stack
				}else{
					double a=0;
					double b=0;
					if(operators.get(s)==4||operators.get(s)<3&&!s.equals("!")){//when calculating +-*/and power
						String x=stack.pop();
						String y=stack.pop();
						if(isNum(x)&&isNum(y)){//when two tokens are both numbers
							a=Double.parseDouble(x);
							b=Double.parseDouble(y);
						}else if(!isNum(x)&&isNum(y)){//when first token is a variable and second is a number
							a=variable.get(x);
							b=Double.parseDouble(y);
						}else if(isNum(x)&&!isNum(y)){//when first token is a number and second is a variable
							a=Double.parseDouble(x);
							b=variable.get(y);
						}else if(!isNum(x)&&!isNum(y)){//when both tokens are variables
							a=variable.get(x);
							b=variable.get(y);
						}
					}else if(operators.get(s)==3&&!s.equals("log")) {//when calculating trigs
						String x=stack.pop();
						if(isNum(x)) {//when the token is a number
							a=Double.parseDouble(x);
						}else if(!isNum(x)) {//when the token is a variable
							a=variable.get(x);
						}
					}else if(s.equals("!")) {//when calculating factorial 
						String x=stack.pop();
						if(isNum(x)) {//when the token is a number
							a=Double.parseDouble(x);
						}else if(!isNum(x)) {//when the token is a variable
							a=variable.get(x);
						}
					}else if(s.equals("log")) {//when calculating natural log
						String x=stack.pop();
						if(isNum(x)) {//when the token is a number
							a=Double.parseDouble(x);
						}else if(!isNum(x)) {//when the token is a variable
							a=variable.get(x);
						}
					}
					switch(s){//push the new number into the stack based on different operations
					case "+" :
						stack.push(String.valueOf(a+b));
						break;
					case "-" :
						stack.push(String.valueOf(b-a));
						break ;
					case "*" :
						stack.push(String.valueOf(a*b));
						break;
					case "/" :
						stack.push(String.valueOf(b/a));
						if(a==0.0) {
							divbyZero=true;
						}
						break ;
					case "^":
						stack.push(String.valueOf(Math.pow(b, a)));
						break;
						//when calculating trigs, assume the input is in radians
					case "#"://representing sin
						stack.push(String.valueOf(Math.sin(Math.toRadians(a))));
						break;
					case "~"://representing cos
						stack.push(String.valueOf(Math.cos(Math.toRadians(a))));
						break;
					case "&"://representing tan
						stack.push(String.valueOf(Math.tan(Math.toRadians(a))));
						break;
					case "!":
						int factorial=1;
						if(a%1!=0||a<0) //report when the user is trying to find the factorial of negative number or decimal
							return"can't find the factorial of decimals or negative numbers";
						for(int q=1;q<=a;q++) {
							factorial=factorial*q;
						}
						stack.push(String.valueOf(factorial));
						break;
					case "β"://representing natural log
						stack.push(String.valueOf(Math.log(a)));
						break;
					}
				}
			}
			if(divbyZero==true) {//report if the denominator is zero
				return "Can't Divide by Zero";
			}
			return stack.pop();
		}catch(EmptyStackException|NullPointerException e) {
			if(e instanceof EmptyStackException) {//an empty stack exception happens when there is an invalid expression
				return "Invalid Expression";
			}else {
				return "Variable not exist";//an null pointer exception happens when there is an non-defined variable
			}
		}
	}
	public static void setVar(String s, String x) {//method to assign value to variable
		try {
			variable.put(s, Double.parseDouble(x) );
		}catch(NumberFormatException e) {
			System.out.println("Invalid Variable");
		}
	}
	public static void showAll() {//method to print all variables and values when use enters show all
		for (String name: variable.keySet()){       
			System.out.println("Variable Name: "+name + " | " + "Value: "+variable.get(name));  
		} 
		if(variable.isEmpty()) 
			System.out.println("No entries for varaibls");//inform the user if the table is empty
	}
	public static void clearAll() {//remove all variables after the user enters clear all
		variable.clear();
	}
	public static void clearVar(String v) {//method to clear a specific variable
		variable.remove(v, variable.get(v));
	}
	public static boolean isVar(String s) {//detect if an input string is a defined variable
		try {
			double d= variable.get(s);
			return true;
		}catch(NullPointerException e) {
			return false;
		}
	}
	public static boolean invalidSpace(String s) {//Method to check if there is an invalid space between two numbers
		for(int i=1; i<s.length()-1;i++) {
			if(s.charAt(i)==' '&&isNum(s.charAt(i-1)+"")&&isNum(s.charAt(i+1)+"")) {
				return true;
			}
		}
		return false;
	}
	public static void start() {//start method that  controls the looping structure
		Scanner sc=new Scanner(System.in);
		setOperations();//set the operator map
		boolean exit=false;
		while(!exit) {//keep going as long as the user does not type exit
			//remove all white spaces from the input if there is any, and replace all sin, cos, tan, log to the single symbol I assigned for them in my operator map
			String str=sc.nextLine().replaceAll("sin", "#").replaceAll("cos", "~").replaceAll("tan", "&").replaceAll("log", "β");
			if(invalidSpace(str)) {
				System.out.println("Invalid Space");
			}else {
				str=str.replaceAll("\\s", "");
			if(str.equals("exit")) {//exit the program is the user chooses to
				exit=true;
				break;
			}else if(str.equals("showall")) {
				showAll();
			}else if(str.contains("=")) {//if there is = sign then it's a variable assigning 
				String[]var=str.split("=");
				for(int i=0;i<var.length-1;i++) {
					if(isVar(var[var.length-1])==true){//set the value to variables repeatedly until the last variable is assigned
						setVar(var[i], variable.get(var[var.length-1]).toString());//when assigning a variable to another defined variable, ex: b=8, a=b
					}else {
						if(evoe(converter(var[var.length-1])).equals("Mismatch Parentheses")) {//report if the assigned value expression has following exceptions
							System.out.println("Mismatch Parentheses for assignment");
							break;
						}else if(evoe(converter(var[var.length-1])).equals("Can't Divide by Zero")){
							System.out.println("Can't divide by zero ");
							break;
						}else{//when assigning a number or expression to a variable
							setVar(var[i], evoe(converter(var[var.length-1])));
						}
					}
				}
			}else if(str.equals("clearall")) {
				clearAll();
			}else if(str.contains("clear")&&!str.contains("all")) {//if the input contains clear but not all, then it must be a clear variable command
				String s=str.substring(5, str.length());
				if(!isVar(s)) {
					System.out.println("Undefined variable");//report if the user is trying to clear an undefined variable
				}else {
				clearVar(s);
				}
			}else if(!str.contains("=")&&!isVar(str)){//when user wants to get the result of the expression
				if(evoe(converter(str)).equals(str)) {
					System.out.println("Invalid Variable");//when the result after calculation is still same with the input, it means it's an invalid variable
				}
				else {
				System.out.println(evoe(converter(str)));
				}
			}else if(isVar(str)&&!str.contains("=")) {//when user is trying to get the value of a single variable directly
				System.out.println(variable.get(str));
			}
		}
		}
		
		System.out.println("Program Exit");
	}
	public static void main(String[] args) {
		start();
	}
}
