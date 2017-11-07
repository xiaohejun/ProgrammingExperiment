package com.calculator.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.Stack;



import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class Calculator extends JFrame implements ActionListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static String exp = ""; // 表达式字符串
	private static String ans = "0"; // 计算出的结果
	private final String[] btnName = { // 按钮的名字
			"CE","Del","(",")",
			"7","8","9","+",
			"4","5","6","-",
			"1","2","3","*",
			"0",".","=","/"
	};
	
	JButton[] btns = new JButton[btnName.length]; // 创建按钮对象数组
	static JTextField display = new JTextField(ans); // 显示文本域
	
	public Calculator() {
		super("计算器"); // 面板名称
		Font font = new Font("Consolas", Font.BOLD, 20); // 创建一个字体对象用于后面方便统一
		
		// ---------------------------------创建面板--------------------------------------------------------------
		// 头部面板
		JPanel pnlHead = new JPanel(new BorderLayout()); // 头部面板,布局是上,下,左,右.中的边界布局
		pnlHead.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // 面板对象的边距
		pnlHead.add(display); // 加入显示答案的文本域
		display.setFont(font); // 设置字体
		
		// 身体面板
		JPanel pnlBody = new JPanel(new GridLayout(5, 4, 10, 10)); // 5行4列的网格式布局,设置作为的间距10个像素
		pnlBody.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));// 面板对象的边距
		for (int i = 0; i < btnName.length; i++) { // 循环创建按钮
			btns[i] = new JButton(btnName[i]);  // 创建按钮对象
			btns[i].setFont(font); // 设置按钮的颜色
			pnlBody.add(btns[i]); // 把按钮加入面板
		}
		btns[18].setForeground(Color.WHITE); // 等号设置字体颜色是白色
		btns[18].setBackground(new Color(72, 10, 221)); // 等号设置背景颜色是蓝色
		btns[0].setForeground(Color.WHITE); // 清空号设置字体颜色是白色
		btns[0].setBackground(Color.RED); // 清空号设置背景颜色是红色
		
		
	    getContentPane().setLayout(new BorderLayout()); // 主窗口的布局是边界布局
	    setLocationByPlatform(true); // 主窗口 出现在位置根据平台习惯
	    getContentPane().add(pnlHead, BorderLayout.NORTH); // 把有显示答案文本域的头部面板放在主窗口的北边
	    getContentPane().add(pnlBody, BorderLayout.CENTER); // 把身体面板放在主窗口的中间,就是那些按钮
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 设置关闭按钮,关闭
	    setSize(500, 500); // 设置窗口大小
	    setVisible(true); // 设置可见
		
	    
	    // 为按钮注册事件监听
	    for (int i = 2; i < btnName.length; i++) {
	    		// 数字按钮和运算符注册监听事件(按钮按下时把按钮的文本加在表达式后面)
			if(i != 18) btns[i].addActionListener(this); // override actionPerformed 
		}
	    // 清空按钮注册监听事件
	    btns[0].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				exp = "";
				display.setText("0");
			}
		});
	    
		// 删除按钮注册监听事件
		btns[1].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(exp.length() != 0) exp = exp.substring(0, exp.length()-1);
				display.setText(exp);
			}
		});
	    
	    // 等于按钮注册监听事件
		btns[18].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cal(exp);
				display.setText(ans);
				exp = "";
			}
		});
	}
	
	// 按钮按下时把按钮的文本加在表达式后面
	@Override
	public void actionPerformed(ActionEvent e) {
		JButton btn = ((JButton)e.getSource());
		exp += btn.getText();
		display.setText(exp);
	}
	
	// 计算表达式
	public static void cal(String exp) {
		if("".equals(exp)) { display.setText("0"); ans = "0"; return;}; // 如果表达式是空
		if(isFalse(exp)) {ans = "exp is false!"; return ;}; // 如果表达式是错的
		Stack<BigDecimal> numStack = new Stack<BigDecimal>(); // 数字 栈
		Stack<Character> operatorStack = new Stack<Character>(); // 运算符 栈		
		String tmpNum = ""; // 记录数据
		exp += "#"; // 在表达式尾部加入结束标志
		int len = exp.length(); // 表达式的长度
		int begin = 0; // 循环的下标
		char c = exp.charAt(0); // 拿表达式的第一个字符
		operatorStack.push('#'); // 把开始标志加入运算符栈
		boolean flag = true; // 表达式遍历晚的标志
		while (c != '#' || !operatorStack.peek().equals('#') && flag) {
			if(!isOperator(c)) { // 是数字
				while (!isOperator((c = exp.charAt(begin)))) { // 循环拿出表达式中的数字
					tmpNum += c;
					begin++;
					if(begin == len) {flag = false; break;}
				}
				numStack.push(new BigDecimal(tmpNum)); // 把字符串变成一个大数字压入栈
				tmpNum = ""; // 置空该字符串
				if(begin == len) break;
				c = exp.charAt(begin); // 读下一个字符,一定是运算符
			}
			else { // 是运算符
				switch (precede(operatorStack.peek(), c)) { // 比较运算符优先级
				case '<': // 栈底运算符优先级低
					operatorStack.push(c); // 运算符入栈
					c = exp.charAt(++begin); // 读下一个字符
					break;
				case '=':
					operatorStack.pop(); // 退掉括号
					c = exp.charAt(++begin); // 读下一个字符
					break;
				case '>': // 栈底运算符优先级高
					// 计算
					numStack.push(Count(numStack.pop(), operatorStack.pop(),numStack.pop()));
					break;
				} // switch
			} // else
		} 
		ans = numStack.pop().toString(); // 返回答案
	}
	
	
	// 计算
	public static BigDecimal Count(BigDecimal a, Character opera, BigDecimal b) {
		BigDecimal res = new BigDecimal("0");
		switch (opera) { // 计算 加 减 乘 除
		case '+':
			res = a.add(b);
			break;
		case '-':
			res = b.subtract(a);
			break;
		case '*':
			res = a.multiply(b);
			break;
		case '/':
			res = b.divide(a);
			break;
		default:
			break;
		}
		return res;
	}

	// 判断字符是否是运算符
	public static boolean isOperator(char c) {
		 return c == '+' || c == '-' || c == '*' || c == '/' || c == '(' || c == ')' || c == '#';
	}
	
	// 判断表达式的正确性
	public static boolean isFalse(String exp) {
		
		return false;
	}
	
	// 返回运算符的优先级
	public static char precede(char a, char b) {
		String op = "+-*/()#"; // 运算符
		char[][] pri= { // 优先级表 // '+','-','*','/','(',')','#'
				{'>','>','<','<','<','>','>'},
				{'>','>','<','<','<','>','>'},
				{'>','>','>','>','<','>','>'},
				{'>','>','>','>','<','>','>'},
				{'<','<','<','<','<','=',' '},
				{'>','>','>','>',' ','>','>'},
				{'<','<','<','<','<',' ','='}
		};
		return pri[op.indexOf(a)][op.indexOf(b)]; // f返回优先级
	}
	
	// 主函数
	public static void main(String[] args) {
	   Calculator cal = new Calculator();
	}
}
