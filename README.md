# Medieval Factions - Enhanced Edition

[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
[![Minecraft](https://img.shields.io/badge/Minecraft-1.19.2+-green.svg)](https://www.spigotmc.org/)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-purple.svg)](https://kotlinlang.org/)

Расширенная версия плагина Medieval Factions с новыми функциями и улучшениями.

## 🚀 Новые возможности

### 💰 Экономическая система
- **Виртуальная валюта** - система монет для игроков
- **Переводы денег** - игроки могут переводить деньги друг другу
- **Топ игроков** - рейтинг самых богатых игроков
- **Безопасные транзакции** - защита от потери средств

### ⚔️ Система команд (Red/Blue/Yellow)
- **Красная и Синяя команды** - с возможностью выбора короля
- **Жёлтые авантюристы** - независимые игроки без команды
- **Королевские бонусы** - короли получают силу в 2 раза быстрее
- **Система рулетки** - случайный выбор короля администратором

### 🎮 Интерактивный гайд
- **Навигационная система** - интерактивное руководство по игре
- **Цветовая палитра** - бронзовые и золотые оттенки
- **Категории команд** - организованная справка

### 🏳️ Флаг выбора команды
- **Автоматическая выдача** - новые игроки получают флаг при входе
- **GUI интерфейс** - удобный выбор команды через интерфейс
- **Защита от потери** - флаг нельзя выбросить или потерять

## 📋 Команды

### Экономика
- `/economy balance [игрок]` - просмотр баланса
- `/economy pay <игрок> <сумма>` - перевод денег
- `/economy top [лимит]` - топ игроков по балансу

### Команды (Teams)
- `/faction teamjoin <команда>` - присоединиться к команде (red/blue/yellow)
- `/faction teaminfo [команда]` - информация о команде
- `/faction teamlist [команда]` - список участников команды
- `/faction kingroll <команда>` - выбрать короля для команды (только red/blue)

### Гайд
- `/faction guide` - интерактивное руководство по игре

## ⚙️ Права доступа

### Экономика
- `mf.economy` - доступ к командам экономики
- `mf.economy.balance` - просмотр баланса
- `mf.economy.balance.other` - просмотр баланса других игроков
- `mf.economy.pay` - перевод денег
- `mf.economy.top` - просмотр топа игроков

### Команды
- `mf.team.join` - присоединение к командам
- `mf.team.info` - просмотр информации о командах
- `mf.team.list` - просмотр списка участников
- `mf.team.flag` - использование флага выбора команды

## 🛠️ Технические особенности

- **Kotlin** - современный язык программирования
- **Gradle** - система сборки
- **JSON хранение** - для экономических данных
- **Многоязычность** - поддержка русского и английского языков
- **Thread-safe** - безопасность в многопоточной среде

## 📦 Установка

1. Скачайте последнюю версию JAR файла из [Releases](https://github.com/DmitryKalibrov/Medieval-Factions-refresh/releases)
2. Поместите файл в папку `plugins/` вашего сервера
3. Перезапустите сервер
4. Настройте права доступа при необходимости

## 🔧 Сборка из исходников

```bash
# Клонирование репозитория
git clone https://github.com/DmitryKalibrov/Medieval-Factions-refresh.git
cd Medieval-Factions-refresh

# Сборка проекта
./gradlew build

# JAR файл будет в build/libs/
```

## 🎯 Совместимость

- **Minecraft**: 1.19.2+
- **Java**: 17+
- **Серверы**: Spigot, Paper, Purpur

## 📝 Лицензия

Этот проект лицензирован под [GNU General Public License v3.0](LICENSE).

## 🤝 Участие в разработке

Форк оригинального проекта [Dans-Plugins/Medieval-Factions](https://github.com/Dans-Plugins/Medieval-Factions) с расширениями и улучшениями.

## 📞 Поддержка

Если у вас есть вопросы или предложения, создайте [Issue](https://github.com/DmitryKalibrov/Medieval-Factions-refresh/issues).

---

**Автор**: [DmitryKalibrov](https://github.com/DmitryKalibrov)  
**Оригинальный проект**: [Medieval Factions by Dans-Plugins](https://github.com/Dans-Plugins/Medieval-Factions)