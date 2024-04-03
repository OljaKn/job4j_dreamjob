package ru.job4j.dreamjob.repository;

import java.time.LocalDateTime;
import ru.job4j.dreamjob.model.Vacancy;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MemoryVacancyRepository implements VacancyRepository {

    private static final MemoryVacancyRepository INSTANCE = new MemoryVacancyRepository();

    private int nextId = 1;

    private final Map<Integer, Vacancy> vacancies = new HashMap<>();

    private MemoryVacancyRepository() {
        save(new Vacancy(
                0, "Intern Java Developer", "Участие в проектах по разработке программного обеспечения", LocalDateTime.now()));
        save(new Vacancy(
                0, "Junior Java Developer", "Разработка бизнес-логики, клиентского кода, отчётов;", LocalDateTime.now()));
        save(new Vacancy(
                0, "Junior+ Java Developer", "Проектирование баз данных", LocalDateTime.now()));
        save(new Vacancy(
                0, "Middle Java Developer", "Участие в проектировании архитектуры новых сервисов", LocalDateTime.now()));
        save(new Vacancy(
                0, "Middle+ Java Developer", "Оптимизация сервисов под высокую нагрузку", LocalDateTime.now()));
        save(new Vacancy(
                0, "Senior Java Developer", "Проектировать и разрабатывать высоконагруженные и отказоустойчивые подсистемы", LocalDateTime.now()));
    }

    public static MemoryVacancyRepository getInstance() {
        return INSTANCE;
    }

    @Override
    public Vacancy save(Vacancy vacancy) {
        vacancy.setId(nextId++);
        vacancies.put(vacancy.getId(), vacancy);
        return vacancy;
    }

    @Override
    public void deleteById(int id) {
        vacancies.remove(id);
    }

    @Override
    public boolean update(Vacancy vacancy) {
        return vacancies.computeIfPresent(vacancy.getId(),
                (id, oldVacancy) -> new Vacancy(oldVacancy.getId(), vacancy.getTitle(), vacancy.getDescription(), vacancy.getCreationDate())) != null;
    }

    @Override
    public Optional<Vacancy> findById(int id) {
        return Optional.ofNullable(vacancies.get(id));
    }

    @Override
    public Collection<Vacancy> findAll() {
        return vacancies.values();
    }
}