import myfirstpackage.*;

class MyFirstClass {
	public static void main(String[] s){
		myfirstpackage.MySecondClass o = new myfirstpackage.MySecondClass(3, 5);

		System.out.println("Значения полей при начальной инициализации" +'\n' +
		"первое число : " + o.get_first_value() +'\n' + "второе число : " +
		 o.get_second_value() +'\n');

		System.out.println(o.operation());
		for (int i = 1; i <= 8; i++) {
 				for (int j = 1; j <= 8; j++) {
     				o.set_first_value(i);
     				o.set_second_value(j);
     				System.out.print(o.operation());
     				System.out.print(" ");
 			}
 			System.out.println();
		}

	}
}

