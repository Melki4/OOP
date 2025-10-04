package ru.ssau.tk._repfor2lab_._OOP_.functions;

public class CompositeFunction implements MathFunction {//конструктор CF(f(x), d(x))
    //выполнит d(f(x))

    private  MathFunction firstFunction;
    private  MathFunction secondFunction;
    /**
     Такая функция должна применять к аргументу
     сначала одну «более простую» функцию, а затем другую – к результату
     первой функции. Математически: h(x)=g(f(x)), где – h(x)
     сложная функция, f(x) – первая функция, может быть как простой, так и
     сложной, g(x) – вторая функция, может быть как простой, так и сложной.
     Именно в таком порядке, так как сначала к x будет применена f, а затем
     к результату – g.
     */
    public CompositeFunction(MathFunction firstFunction, MathFunction secondFunction) {
        this.firstFunction = firstFunction;
        this.secondFunction = secondFunction;
    }

    public double apply(double x){
        return secondFunction.apply(firstFunction.apply(x));
    }
}
