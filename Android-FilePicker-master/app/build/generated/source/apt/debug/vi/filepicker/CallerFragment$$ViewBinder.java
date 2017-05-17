// Generated code from Butter Knife. Do not modify!
package vi.filepicker;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class CallerFragment$$ViewBinder<T extends vi.filepicker.CallerFragment> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131624052, "method 'pickDocClicked'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.pickDocClicked(p0);
        }
      });
  }

  @Override public void unbind(T target) {
  }
}
