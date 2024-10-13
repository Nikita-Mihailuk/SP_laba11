import java.util.Random;

class Account {
    private int balance = 0;
    public synchronized void addBalance(int amount) {
        balance += amount;
        System.out.println("Пополнено: " + amount + ". Текущий баланс: " + balance);
        notifyAll(); // уведомляем остальные потоки о переходе из режима ожидания
    }
    public synchronized void subBalance(int amount) {
        while (balance < amount) {
            try {
                wait(); // ожидание выполнения работы второго потока
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        balance -= amount;
        System.out.println("Снято: " + amount + ". Оставшийся баланс: " + balance);
    }
}

public class Main {
    public static void main(String[] args) {
        Account account = new Account();

        Thread thread1 = new Thread(() -> {
            Random random = new Random();
            for (int i = 0; i < 20; i++) {
                account.addBalance(random.nextInt(1000));
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        Thread thread2 = new Thread(() -> {
            while (true) account.subBalance(2000);
        });

        thread1.start();
        thread2.start();
    }
}
