package controllers;
import engine.MainEngine;
import org.springframework.stereotype.Service;

@Service
public class MainEngineService {

    private MainEngine mainEngine;

    public MainEngineService() {
        this.mainEngine = new MainEngine();
    }
}
