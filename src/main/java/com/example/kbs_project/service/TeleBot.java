package com.example.kbs_project.service;

import com.example.kbs_project.entity.Law;
import com.example.kbs_project.repository.LawRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import javax.security.auth.callback.Callback;
import java.util.ArrayList;
import java.util.List;

@Service
public class TeleBot extends TelegramLongPollingBot {

    @Autowired
    private LawRepository lawRepository;

    @PostConstruct
    public void registerBot() {
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(this);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return "CHTDTTT - Tư vấn bệnh về tiêu hóa";
    }

    @Override
    public String getBotToken() {
        return "5898849974:AAFUlCwFmG4sEIfFnsgAUH-zVE2Io5fxkCw";
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.getMessage() != null && update.getMessage().hasText()){
            String msg = update.getMessage().getText();
            System.out.println(msg);
            switch (msg){
                case "/start":
                    startBot(update);
                    break;
                case "/tuvanngay":
                    startAdvise(update);
                    break;
                default:
                    return;
            }
        }
        else if (update.hasCallbackQuery()){
            CallbackQuery callbackQuery = update.getCallbackQuery();
            String data = callbackQuery.getData();
            if (data.equals("/tuvanngay"))
                startAdvise(update);
            else if (data.equals("/startquestion")) // bat dau cau hoi dau tien
                ask(update, null);
            else if (data.contains("answer")){ // nguoi dung tra loi
                clearKeyboardMarkup(update);
                String[] dataSplit = data.split("-");
                answer(Integer.parseInt(dataSplit[1]), dataSplit[2], update);
            }
            // answer-lawId-yes
        }
    }

    private void answer(int lawId, String answer, Update update){

        // chat bot
        Law law = lawRepository.getById(lawId);

        if (answer.equals("yes"))
            law = lawRepository.getById(law.getYes());
        else
            law = lawRepository.getById(law.getNo());

        if (law.getSymptom() != null)
            ask(update, law);
        else
            conlusion(update, law);
    }

    private void ask(Update update, Law law){
        if (law == null)
            law = lawRepository.getById(1);

        String text = "Bạn có bị " + law.getSymptom().getName() + " không?";

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getCallbackQuery().getMessage().getChatId());
        sendMessage.setText(text);
        sendMessage.setParseMode(ParseMode.MARKDOWN);

        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> inlineButtons = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonList = new ArrayList<>();

        InlineKeyboardButton button1 = new InlineKeyboardButton();
        InlineKeyboardButton button2 = new InlineKeyboardButton();
        button1.setText("Có");
        button1.setCallbackData("answer-" + law.getId() + "-yes");
        button2.setText("Không");
        button2.setCallbackData("answer-" + law.getId() + "-no");

        keyboardButtonList.add(button1);
        keyboardButtonList.add(button2);

        inlineButtons.add(keyboardButtonList);
        keyboardMarkup.setKeyboard(inlineButtons);
        sendMessage.setReplyMarkup(keyboardMarkup);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    private void startAdvise(Update update){
        String text = "Bạn sẽ phải trả lời các câu hỏi bằng cách chọn vào nút Có/Không trên màn hình.\n" +
                "Bấm vào nút Bắt đầu tư vấn để bắt đầu";
        Long chatId = update.hasMessage() ? update.getMessage().getChatId() :
                                            update.getCallbackQuery().getMessage().getChatId();

        SendMessage sendMessage  = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        sendMessage.setParseMode(ParseMode.MARKDOWN);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> inlineButtons = new ArrayList<>();
        List<InlineKeyboardButton> inlineKeyboardButtonList = new ArrayList<>();

        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText("Bắt đầu tư vấn");
        inlineKeyboardButton.setCallbackData("/startquestion");
        inlineKeyboardButtonList.add(inlineKeyboardButton);

        inlineButtons.add(inlineKeyboardButtonList);
        inlineKeyboardMarkup.setKeyboard(inlineButtons);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    private void startBot(Update update){
        String text = "Xin chào, đây là Bot tư vấn khám chữa bệnh về tiêu hóa.\n" +
                "Bot sẽ đưa ra những câu hỏi và dựa vào câu trả lời của bạn để phỏng đoán bệnh và đưa ra lời khuyên.\n" +
                "Để bắt đầu tư vấn vui sử dụng một trong những cách sau đây:\n" +
                "\t1. Chọn 'Tư vấn ngay' trong Menu\n" +
                "\t2. Chat hoặc bấm vào '/tuvanngay'\n" +
                "\t3. Bấm Tư vấn ngay";

        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(text);
        sendMessage.setParseMode(ParseMode.MARKDOWN);
        sendMessage.setChatId(update.getMessage().getChatId());

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> inlineButtons = new ArrayList<>();

        List<InlineKeyboardButton> inlineKeyboardButtonList = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText("Tư vấn ngay");
        inlineKeyboardButton.setCallbackData("/tuvanngay");
        inlineKeyboardButtonList.add(inlineKeyboardButton);


        inlineButtons.add(inlineKeyboardButtonList);
        inlineKeyboardMarkup.setKeyboard(inlineButtons);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    private void clearKeyboardMarkup(Update update){
        Long chatID = update.getCallbackQuery().getMessage().getChatId();
        EditMessageReplyMarkup message = new EditMessageReplyMarkup();

        message.setChatId(chatID);
        message.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
        message.setInlineMessageId(update.getCallbackQuery().getInlineMessageId());


        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> inlineButtons = new ArrayList<>();

        List<InlineKeyboardButton> inlineKeyboardButtonList = new ArrayList<>();
        InlineKeyboardButton btn1 = new InlineKeyboardButton();
        InlineKeyboardButton btn2 = new InlineKeyboardButton();
        btn1.setText("Có");
        btn1.setCallbackData("-");
        btn2.setText("Không");
        btn2.setCallbackData("-");
        inlineKeyboardButtonList.add(btn1);
        inlineKeyboardButtonList.add(btn2);

        inlineButtons.add(inlineKeyboardButtonList);
        inlineKeyboardMarkup.setKeyboard(inlineButtons);

        message.setReplyMarkup(inlineKeyboardMarkup);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatID);
        sendMessage.setText(update.getCallbackQuery().getData().split("-")[2].equals("yes") ? "Có" : "Không");
        try {
            execute(message);
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    private void conlusion(Update update, Law law){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getCallbackQuery().getMessage().getChatId());

        String diseaseName = law.getDiseases().getName();
        if (law.getReliability() != null)
            diseaseName = "Kết luận: Có khả năng bạn đã mắc bệnh " + diseaseName;
        else
            diseaseName = "Kết luận: Bạn đã mắc bệnh " + diseaseName;

        String text = diseaseName + ".\n\n" +
                "Bạn có thể tham khảo một số cách khắc phục, phòng ngừa dưới đây: ";
        String[] solutions = law.getDiseases().getSolutions().split("-");
        for (int i=0; i<solutions.length; i++){
            text += "\n- " + solutions[i];
        }

        sendMessage.setText(text);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
