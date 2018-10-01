package clovero;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Buttons {

    MAIN_MENU("Menu", "Menu"),
    GAME("Mulai Bermain", "Mulai Bermain"),
    MY_SCORE("Skor Saya", "Skor Saya"),
    HIGH_SCORES("Skor Terbaik", "Skor Terbaik"),
    INSTRUCTIONS("Instruksi", "Instruksi"),
    HELP("Help", "Help"),
    HINT("Petunjuk", "Petunjuk"),
    CONTINUE("Lanjut", "Lanjut"),
    CHANGE_CATEGORY("Ganti Kategori", "Ganti Kategori"),
    YES("Ya", "Ya"),
    NO("Tidak", "Tidak"),
    CANCEL("Batal", "Batal"),
    EXIT("Keluar", "Keluar");

    private String label;
    private String value;

}
