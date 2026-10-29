package by.khaltura.lab4.service;

import java.math.BigDecimal;
import java.time.LocalDate;

import by.khaltura.lab4.dao.BidDAO;
import by.khaltura.lab4.dao.TaskDAO;
import by.khaltura.lab4.model.AppUser;
import by.khaltura.lab4.model.Bid;
import by.khaltura.lab4.model.BidStatus;
import by.khaltura.lab4.model.Task;
import by.khaltura.lab4.model.TaskStatus;

// Правила работы с откликами: подача, отзыв, выбор исполнителя
public class BidService {

    private final BidDAO bidDAO = new BidDAO();
    private final TaskDAO taskDAO = new TaskDAO();

    public Bid submit(Integer taskId, AppUser executor, BigDecimal price, LocalDate deadline, String text) {
        if (!executor.isVerified()) {
            throw new IllegalStateException("Сначала подтвердите личность: обратитесь к администратору");
        }
        Task task = taskDAO.findTaskById(taskId);
        if (task == null) throw new IllegalArgumentException("Задача не найдена");
        if (task.getCustomer().equals(executor)) throw new IllegalStateException("Нельзя откликнуться на собственную задачу");
        if (task.getStatus() != TaskStatus.PUBLISHED) throw new IllegalStateException("Задача не принимает отклики");
        if (price.signum() <= 0) throw new IllegalArgumentException("Цена должна быть больше нуля");
        if (deadline.isBefore(LocalDate.now())) throw new IllegalArgumentException("Срок не может быть в прошлом");
        if (text.length() < 10) throw new IllegalArgumentException("Текст предложения – не короче 10 символов");

        Bid existing = bidDAO.findOwnBid(taskId, executor.getUserId());
        if (existing != null && existing.getStatus() != BidStatus.CANCELLED) {
            throw new IllegalStateException("Вы уже откликнулись на эту задачу");
        }
        if (existing != null) {
            // Отозванный ранее отклик можно подать заново – обновляем ту же запись
            existing.setProposedPrice(price);
            existing.setDeadline(deadline);
            existing.setProposalText(text);
            existing.setStatus(BidStatus.PENDING);
            return bidDAO.updateBid(existing);
        }
        return bidDAO.addBid(new Bid(task, executor, price, deadline, text));
    }

    // Отозвать можно только свой отклик, по которому ещё нет решения
    public void cancel(Integer bidId, AppUser executor) {
        Bid bid = bidDAO.findBidById(bidId);
        if (bid == null || !bid.getExecutor().equals(executor)) throw new IllegalArgumentException("Отклик не найден");
        if (bid.getStatus() != BidStatus.PENDING) throw new IllegalStateException("Отозвать можно только ожидающий отклик");
        bid.setStatus(BidStatus.CANCELLED);
        bidDAO.updateBid(bid);
    }

    // Заказчик выбирает исполнителя по своей опубликованной задаче
    public Bid select(Integer bidId, AppUser customer) {
        Bid bid = bidDAO.findBidById(bidId);
        if (bid == null || !bid.getTask().getCustomer().equals(customer)) {
            throw new IllegalArgumentException("Отклик не найден среди откликов на ваши задачи");
        }
        if (bid.getStatus() != BidStatus.PENDING) throw new IllegalStateException("Этот отклик уже неактуален");
        TaskService.checkTransition(bid.getTask(), TaskStatus.IN_PROGRESS);
        taskDAO.acceptBid(bidId);
        return bidDAO.findBidById(bidId);
    }
}
