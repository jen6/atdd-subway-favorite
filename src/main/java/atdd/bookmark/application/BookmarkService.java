package atdd.bookmark.application;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import atdd.bookmark.application.dto.BookmarkResponseView;
import atdd.bookmark.application.dto.BookmarkSimpleResponseView;
import atdd.bookmark.entity.Bookmark;
import atdd.bookmark.repository.BookmarkRepository;
import atdd.path.application.dto.StationResponseView;
import atdd.path.application.exception.NoDataException;
import atdd.path.domain.Station;
import atdd.path.repository.StationRepository;

@Service
public class BookmarkService {
  private BookmarkRepository bookmarkRepository;
  private StationRepository stationRepository;

  public BookmarkService(BookmarkRepository bookmarkRepository, StationRepository stationRepository) {
    this.bookmarkRepository = bookmarkRepository;
    this.stationRepository = stationRepository;
  }

  public BookmarkSimpleResponseView addStationBookmark(Long userID, Long stationID) {
    Station sourceStation = stationRepository.findById(
        stationID
        ).orElseThrow(NoDataException::new);

    Bookmark created = bookmarkRepository.save(
        new Bookmark(userID, sourceStation)
        );

    return new BookmarkSimpleResponseView(
        created.getId(),
        new StationResponseView(
          sourceStation.getId(), sourceStation.getName()
          )
        );
  }

  public BookmarkResponseView retrieveAllBookmarks(Long userID) {
    List<BookmarkSimpleResponseView> bookmarks = bookmarkRepository.findByUserID(userID).stream().map(
          bookmark-> {
            Station sourceStation = bookmark.getSourceStation();
            Station targetStation = bookmark.getTargetStation();
            if (targetStation == null) {
              return new BookmarkSimpleResponseView(
                  bookmark.getId(),
                  new StationResponseView(
                    sourceStation.getId(), sourceStation.getName()));
            }
            return new BookmarkSimpleResponseView(
                bookmark.getId(),
                new StationResponseView(
                  sourceStation.getId(), sourceStation.getName()),
                new StationResponseView(
                  targetStation.getId(), targetStation.getName())
                );
          }).collect(Collectors.toList());

      return new BookmarkResponseView(bookmarks);
  }
}
