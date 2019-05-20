package app;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import structures.Stack;
import java.util.StringTokenizer;
public class Expression {

	public static String delims = " \t*+-/()[]";
			
    /**
     * Populates the vars list with simple variables, and arrays lists with arrays
     * in the expression. For every variable (simple or array), a SINGLE instance is created 
     * and stored, even if it appears more than once in the expression.
     * At this time, values for all variables and all array items are set to
     * zero - they will be loaded from a file in the loadVariableValues method.
     * 
     * @param expr The expression
     * @param vars The variables array list - already created by the caller
     * @param arrays The arrays array list - already created by the caller
     */
    public static boolean isNumber(String str) {
    	 return str.matches("-?\\d+(\\.\\d+)?");
    }
    
    public static void makeVariableLists(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	
    	 String expr2 = expr;
    	  StringTokenizer st = new StringTokenizer(expr2, delims);
    	 
    	while(st.hasMoreTokens()) {
    		String temp = st.nextToken();
    		if(isNumber(temp)== false) {
    			if(temp.length() == 1) {
    				vars.add(new Variable(temp));
    			}
    			
    			else {
    				char nextChar = expr2.charAt(expr2.indexOf(temp) + 1);
    				if( nextChar == '[') {
    			
    					arrays.add(new Array(temp));
    				
    			}
    		
    				else {
    					vars.add(new Variable(temp));
    		}
    			
    	}
    	}
    }
    
    	
    	
    	
    	
    	/** DO NOT create new vars and arrays - they are already created before being sent in
    	 ** to this method - you just need to fill them in.
    	 **/
    }
    
    /**
     * Loads values for variables and arrays in the expression
     * 
     * @param sc Scanner for values input
     * @throws IOException If there is a problem with the input 
     * @param vars The variables array list, previously populated by makeVariableLists
     * @param arrays The arrays array list - previously populated by makeVariableLists
     */
    public static void 
    loadVariableValues(Scanner sc, ArrayList<Variable> vars, ArrayList<Array> arrays) 
    throws IOException {
        while (sc.hasNextLine()) {
            StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
            int numTokens = st.countTokens();
            String tok = st.nextToken();
            Variable var = new Variable(tok);
            Array arr = new Array(tok);
            int vari = vars.indexOf(var);
            int arri = arrays.indexOf(arr);
            if (vari == -1 && arri == -1) {
            	continue;
            }
            int num = Integer.parseInt(st.nextToken());
            if (numTokens == 2) { // scalar symbol
                vars.get(vari).value = num;
            } else { // array symbol
            	arr = arrays.get(arri);
            	arr.values = new int[num];
                // following are (index,val) pairs
                while (st.hasMoreTokens()) {
                    tok = st.nextToken();
                    StringTokenizer stt = new StringTokenizer(tok," (,)");
                    int index = Integer.parseInt(stt.nextToken());
                    int val = Integer.parseInt(stt.nextToken());
                    arr.values[index] = val;              
                }
            }
        }
    }
    
    /**
     * Evaluates the expression.
     * 
     * @param vars The variables array list, with values for all variables in the expression
     * @param arrays The arrays array list, with values for all array items
     * @return Result of evaluation
     */
    public static float 
    evaluate(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	Stack <String> op = new Stack<String>();
    	Stack <Float> val = new Stack<Float>();
    	
    	for(Variable var: vars) {
    		expr = expr.replaceAll(var.name, Integer.toString(var.value));
    	}
    	
    	if(expr.matches("[0-9]+")) {
    		return Float.parseFloat(expr);
    	}
    	
    	expr = expr.replaceAll(" ", "");
    	if(expr.indexOf('(') == -1 && expr.indexOf('[') == -1) {
    		for(int i = 0; i<expr.length();i++) {
    			String s = Character.toString(expr.charAt(i)); 
    			if(s.equals("*") || s.equals("/")) {
    				op.push(s); 
    			}
    			else if(s.equals("+") || s.equals("-")) {
    				op.push(s); 
    			}
    			else {
    				
    				
    				if((expr.indexOf(expr.charAt(i))-1) == -1) {
						val.push(Float.parseFloat(s));
					} else {
						
						if((expr.charAt(i-1)) != '*' && (expr.charAt(i-1)) != '/' && (expr.charAt(i-1)) != '+' && (expr.charAt(i-1)) != '-') {
    					 
        					val.push(Float.parseFloat((String.valueOf(Math.round(val.pop())) + s)));
    				    } else {
					        val.push(Float.parseFloat(s));
    				}
						continue;
    			}
    				
    		}
    	}
    		
    		evalSub(op,val);
    	
    	}
    	return val.pop();
    }
    

    public static void evalSub (Stack<String> op , Stack<Float> val)  {
    	
    	
    	while(!op.isEmpty()) {
    		
    		String solve = op.pop();
    		if(solve.equals("*")) {
    			if(!op.isEmpty()) {
    				if(op.peek().equals("*")) {
    		
    			float tempVal = val.pop();
    			val.push(val.pop()*val.pop());
    			val.push(tempVal);
    		} else if(op.peek().equals("/")) {
    			String tempOp = solve;
    			solve = op.pop();
    			op.push(tempOp);
    			float tempVal = val.pop();
    			float second = val.pop();
    			float first = val.pop();
    			val.push(first/second);
    			val.push(tempVal);
    			
    		} else {
    			val.push(val.pop() * val.pop());
    		}
    	}
    	else {
        		val.push(val.pop() * val.pop());
        		}
    	}
    	else if(solve.equals("/")) {
    		if(!op.isEmpty()) {
    			if(op.peek().equals("*")) {
    				String tempOp = solve;
        			solve = op.pop();
        			op.push(tempOp);
        			float tempVal = val.pop();
        			val.push(val.pop()*val.pop());
        			val.push(tempVal);
    			} else if(op.peek().equals("/")){
    				
        			float tempVal = val.pop();
        			float second = val.pop();
        			float first = val.pop();
        			val.push(first/second);
        			val.push(tempVal);
    			} else {
    				float second = val.pop();
        			float first = val.pop();		
        			val.push(first / second);
        	}
    			}
    	        
    		else {
				float second = val.pop();
    			float first = val.pop();		
    			val.push(first / second);
    	}
    	}
    		else if(solve.equals("+")) {
    			if(!op.isEmpty() ) {
    				
					if(op.peek().equals("*")) {
    				 String tempOp = solve;
        			solve = op.pop();
        			op.push(tempOp);
        			float tempVal = val.pop();
        			val.push(val.pop()*val.pop());
        			val.push(tempVal);
    			} else if(op.peek().equals("/")) {
        			String tempOp = solve;
        			solve = op.pop();
        			op.push(tempOp);
        			float tempVal = val.pop();
        			float second = val.pop();
        			float first = val.pop();
        			val.push(first/second);
        			val.push(tempVal);
    			} else if(op.peek().equals("-")) {
    				String temp2 = op.pop();
    				if(!op.isEmpty()) {
    					
    				
    					if(op.peek().equals("*")) {
    					
          				 String tempOp = solve;
              			solve = op.pop();
              			op.push(temp2);
              			op.push(tempOp);
              			float tempVal = val.pop();
              			val.push(val.pop()*val.pop());
              			val.push(tempVal);
    				} else if (op.pop().equals("/")) {
    					String tempOp = solve;
               			solve = op.pop();
               			op.push(temp2);
               			op.push(tempOp);
               			float tempVal = val.pop();
               			float tempVal2 = val.pop();
               			float second = val.pop();
               			float first = val.pop();
               			val.push(first/second);
               			val.push(tempVal2);
               			val.push(tempVal);
       				} else {
       					op.push(temp2);
       					String tempOp = solve;
            			solve = op.pop();
            			op.push(tempOp);
            			float tempVal = val.pop();
            			float second = val.pop();
            			float first = val.pop();
            			val.push(first-second);
            			val.push(tempVal);
       				}
    			}
    				op.push(temp2);
    				String tempOp = solve;
        			solve = op.pop();
        			op.push(tempOp);
        			float tempVal = val.pop();
        			float second = val.pop();
        			float first = val.pop();
        			val.push(first-second);
        			val.push(tempVal);
    			}
    			
    			
       					else if(op.peek().equals("/")) {
       				
               			String tempOp = solve;
               			solve = op.pop();
               			op.push(tempOp);
               			float tempVal = val.pop();
               			float second = val.pop();
               			float first = val.pop();
               			val.push(first/second);
               			val.push(tempVal);
       				}
    			}
    				
    			 else {
    				val.push(val.pop() + val.pop());
    			}
    	}
    			
    			
    		
    		
    	
    		else if(solve.equals("-")) {
    				if(!op.isEmpty() ) {
    				
    					if(op.peek().equals("*")) {
    				 String tempOp = solve;
        			solve = op.pop();
        			op.push(tempOp);
        			float tempVal = val.pop();
        			val.push(val.pop()*val.pop());
        			val.push(tempVal);
    			} else if(op.peek().equals("/")) {
        			String tempOp = solve;
        			solve = op.pop();
        			op.push(tempOp);
        			float tempVal = val.pop();
        			float second = val.pop();
        			float first = val.pop();
        			val.push(first/second);
        			val.push(tempVal);
    			} else if(op.peek().equals("+")) {
    				String temp2 = op.pop();
    				if(op.peek().equals("*")) {
       				 String tempOp = solve;
           			solve = op.pop();
           			op.push(temp2);
           			op.push(tempOp);
           			float tempVal = val.pop();
           			val.push(val.pop()*val.pop());
           			val.push(tempVal);
    				} else if(op.peek().equals("/")) {
            			String tempOp = solve;
            			solve = op.pop();
            			op.push(temp2);
            			op.push(tempOp);
            			float tempVal = val.pop();
            			float tempVal2 = val.pop();
            			float second = val.pop();
            			float first = val.pop();
            			val.push(first/second);
            			val.push(tempVal2);
            			val.push(tempVal);
    				} else {
    				op.push(temp2);
    				String tempOp = solve;
        			solve = op.pop();
        			op.push(tempOp);
        			float tempVal = val.pop();
        			float second = val.pop();
        			float first = val.pop();
        			val.push(first+second);
        			val.push(tempVal);
    			}
    			}
    				
    				else {
    				float tempVal = val.pop();
        			float second = val.pop();
        			float first = val.pop();
        			val.push(first-second);
        			val.push(tempVal);
    				
    			}
    		}
    				 else {
    			
    				float second = val.pop();
        			float first = val.pop();
        			val.push(first-second);
    	}
    		}
    		}
    	}
    }

     
    

    
    	
    	
    	
    		
    	
    


