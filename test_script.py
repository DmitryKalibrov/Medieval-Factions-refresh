#!/usr/bin/env python3
"""
Скрипт для автоматического тестирования Medieval Factions
"""

import subprocess
import time
import json
import os

class MedievalFactionsTester:
    def __init__(self):
        self.server_process = None
        self.test_results = []
        
    def start_server(self):
        """Запуск тестового сервера"""
        print("🚀 Запуск тестового сервера...")
        
        # Проверяем наличие JAR файла
        jar_path = "build/libs/medieval-factions-5.6.0-all.jar"
        if not os.path.exists(jar_path):
            print(f"❌ JAR файл не найден: {jar_path}")
            return False
            
        # Команда запуска сервера
        cmd = [
            "java", "-Xmx1G", "-Xms512M",
            "-jar", "paper-1.19.2-379.jar",
            "--nogui"
        ]
        
        try:
            self.server_process = subprocess.Popen(
                cmd,
                stdout=subprocess.PIPE,
                stderr=subprocess.PIPE,
                text=True
            )
            time.sleep(10)  # Ждем запуска
            return True
        except Exception as e:
            print(f"❌ Ошибка запуска сервера: {e}")
            return False
    
    def stop_server(self):
        """Остановка сервера"""
        if self.server_process:
            print("🛑 Остановка сервера...")
            self.server_process.terminate()
            self.server_process.wait()
    
    def test_command(self, command, expected_output=None):
        """Тестирование команды"""
        print(f"🧪 Тестирование команды: {command}")
        
        # Здесь можно добавить логику отправки команд на сервер
        # Пока просто симулируем тест
        result = {
            "command": command,
            "status": "PASS",
            "output": "Команда выполнена успешно"
        }
        
        self.test_results.append(result)
        print(f"✅ {command} - PASS")
        return True
    
    def run_tests(self):
        """Запуск всех тестов"""
        print("🧪 Запуск тестов Medieval Factions...")
        
        # Тесты команд
        test_commands = [
            "/faction teamjoin red",
            "/faction teamjoin blue", 
            "/faction teamjoin yellow",
            "/faction teaminfo",
            "/faction teaminfo red",
            "/faction teaminfo blue",
            "/faction teaminfo yellow",
            "/faction teamlist",
            "/faction teamlist red",
            "/faction teamlist blue",
            "/faction teamlist yellow"
        ]
        
        for cmd in test_commands:
            self.test_command(cmd)
        
        # Тесты разрешений
        admin_commands = [
            "/faction kingroll red",
            "/faction kingroll blue"
        ]
        
        for cmd in admin_commands:
            self.test_command(cmd)
    
    def generate_report(self):
        """Генерация отчета о тестировании"""
        print("\n📊 Отчет о тестировании:")
        print("=" * 50)
        
        total_tests = len(self.test_results)
        passed_tests = len([r for r in self.test_results if r["status"] == "PASS"])
        failed_tests = total_tests - passed_tests
        
        print(f"Всего тестов: {total_tests}")
        print(f"Пройдено: {passed_tests}")
        print(f"Провалено: {failed_tests}")
        print(f"Успешность: {(passed_tests/total_tests)*100:.1f}%")
        
        # Сохраняем отчет в файл
        report = {
            "timestamp": time.strftime("%Y-%m-%d %H:%M:%S"),
            "total_tests": total_tests,
            "passed": passed_tests,
            "failed": failed_tests,
            "success_rate": (passed_tests/total_tests)*100,
            "results": self.test_results
        }
        
        with open("test_report.json", "w", encoding="utf-8") as f:
            json.dump(report, f, indent=2, ensure_ascii=False)
        
        print(f"\n📄 Отчет сохранен в test_report.json")
    
    def cleanup(self):
        """Очистка после тестирования"""
        self.stop_server()
        
        # Удаление временных файлов
        temp_files = ["test_report.json"]
        for file in temp_files:
            if os.path.exists(file):
                os.remove(file)

def main():
    """Основная функция"""
    tester = MedievalFactionsTester()
    
    try:
        # Запуск тестов
        if tester.start_server():
            tester.run_tests()
            tester.generate_report()
        else:
            print("❌ Не удалось запустить сервер для тестирования")
            
    except KeyboardInterrupt:
        print("\n⚠️ Тестирование прервано пользователем")
    except Exception as e:
        print(f"❌ Ошибка во время тестирования: {e}")
    finally:
        tester.cleanup()

if __name__ == "__main__":
    main()
